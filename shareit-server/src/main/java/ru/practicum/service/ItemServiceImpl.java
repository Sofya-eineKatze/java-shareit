package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.BookingDto;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.model.Booking;
import ru.practicum.model.Comment;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;
import ru.practicum.model.User;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        List<Item> items = itemRepository.findByOwner(ownerId);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        Map<Long, List<CommentDto>> commentsByItemId = commentRepository.findByItemIdInOrderByCreatedAsc(itemIds).stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(commentMapper::toDto, Collectors.toList())
                ));

        List<Booking> allBookings = bookingRepository.findAllByItemIdInAndNotRejected(itemIds);

        Map<Long, Booking> lastBookingByItemId = new HashMap<>();
        Map<Long, Booking> nextBookingByItemId = new HashMap<>();
        for (Booking booking : allBookings) {
            Long itemId = booking.getItem().getId();
            if (!booking.getStart().isAfter(now)) {
                Booking current = lastBookingByItemId.get(itemId);
                if (current == null || booking.getStart().isAfter(current.getStart())) {
                    lastBookingByItemId.put(itemId, booking);
                }
            } else {
                Booking current = nextBookingByItemId.get(itemId);
                if (current == null || booking.getStart().isBefore(current.getStart())) {
                    nextBookingByItemId.put(itemId, booking);
                }
            }
        }

        return items.stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toDto(item);
                    dto.setComments(commentsByItemId.getOrDefault(item.getId(), List.of()));
                    Booking last = lastBookingByItemId.get(item.getId());
                    if (last != null) {
                        dto.setLastBooking(toBookingDto(last));
                    }
                    Booking next = nextBookingByItemId.get(item.getId());
                    if (next != null) {
                        dto.setNextBooking(toBookingDto(next));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedAsc(itemId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        ItemDto dto = itemMapper.toDto(item);
        dto.setComments(comments);

        if (item.getOwner().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            bookingRepository.findLastBookingByItemId(itemId, now)
                    .ifPresent(booking -> dto.setLastBooking(toBookingDto(booking)));
            bookingRepository.findNextBookingByItemId(itemId, now)
                    .ifPresent(booking -> dto.setNextBooking(toBookingDto(booking)));
        }

        return dto;
    }

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "Запрос с id " + itemDto.getRequestId() + " не найден"));
        }

        Item item = itemMapper.toEntity(itemDto, ownerId, request);
        Item saved = itemRepository.save(item);
        log.info("Создана вещь с id {} для пользователя {}", saved.getId(), ownerId);
        return itemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));
        if (!item.getOwner().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        Item updated = itemRepository.save(item);
        log.info("Обновлена вещь с id {}", updated.getId());
        return itemMapper.toDto(updated);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.searchByText(text).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentCreateDto commentCreateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                userId, itemId, LocalDateTime.now());
        if (!hasBooked) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        log.info("Добавлен комментарий к вещи {} от пользователя {}", itemId, userId);
        return commentMapper.toDto(saved);
    }

    private BookingDto toBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(new BookingDto.BookerInfo(booking.getBooker().getId()));
        dto.setItem(new BookingDto.ItemInfo(booking.getItem().getId(), booking.getItem().getName()));
        return dto;
    }
}
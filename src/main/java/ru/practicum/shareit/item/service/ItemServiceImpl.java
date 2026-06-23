package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        List<Item> items = itemRepository.findByOwner(ownerId);
        return items.stream()
                .map(item -> {
                    ItemDto dto = itemMapper.toDto(item, commentRepository.findByItemId(item.getId()));
                    addBookingInfo(dto, item, ownerId);
                    return dto;
                })
                .toList();
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        ItemDto dto = itemMapper.toDto(item, comments);
        addBookingInfo(dto, item, userId);

        return dto;
    }

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        Item item = itemMapper.toEntity(itemDto, ownerId);
        Item saved = itemRepository.save(item);
        log.info("Создана вещь с id {} для пользователя {}", saved.getId(), ownerId);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existing.getOwner().equals(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем вещи");
        }

        itemMapper.updateEntity(existing, itemDto);
        Item updated = itemRepository.save(existing);
        log.info("Обновлена вещь с id {}", updated.getId());
        return itemMapper.toDto(updated);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.searchByText(text);
        return items.stream()
                .map(item -> itemMapper.toDto(item, commentRepository.findByItemId(item.getId())))
                .toList();
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, String text) {
        log.info("Добавление комментария: itemId={}, userId={}, text={}", itemId, userId, text);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Проверяем, что пользователь брал вещь в аренду
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = commentMapper.toEntity(text, item, user);
        Comment saved = commentRepository.save(comment);
        log.info("Сохранён комментарий: id={}, text={}", saved.getId(), saved.getText());
        return commentMapper.toDto(saved);
    }

    private void addBookingInfo(ItemDto dto, Item item, Long userId) {
        // Только владелец видит информацию о бронированиях
        if (!item.getOwner().equals(userId)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(item.getId());

        // lastBooking — последнее завершённое бронирование
        bookings.stream()
                .filter(b -> b.getEnd().isBefore(now) && b.getStatus() == BookingStatus.APPROVED)
                .findFirst()
                .ifPresent(b -> dto.setLastBooking(mapToBookingInfo(b)));

        // nextBooking — ближайшее будущее бронирование
        bookings.stream()
                .filter(b -> b.getStart().isAfter(now) && b.getStatus() == BookingStatus.APPROVED)
                .reduce((first, second) -> second)
                .ifPresent(b -> dto.setNextBooking(mapToBookingInfo(b)));
    }

    private ItemDto.BookingInfo mapToBookingInfo(Booking booking) {
        return new ItemDto.BookingInfo(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getStatus().name()
        );
    }
}
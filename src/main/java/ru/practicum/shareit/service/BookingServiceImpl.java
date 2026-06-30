package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.BookingRequestDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.mapper.BookingMapper;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.status.BookingStatus;
import ru.practicum.shareit.booking.strategy.BookingStateContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.model.Item;
import ru.practicum.shareit.repository.ItemRepository;
import ru.practicum.shareit.model.User;
import ru.practicum.shareit.repository.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final BookingStateContext stateContext;

    @Override
    public BookingResponseDto createBooking(Long bookerId, BookingRequestDto requestDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(requestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().equals(bookerId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        if (requestDto.getStart().isAfter(requestDto.getEnd()) || requestDto.getStart().equals(requestDto.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        Booking booking = bookingMapper.toEntity(requestDto, BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        Booking saved = bookingRepository.save(booking);
        log.info("Создано бронирование с id {} для вещи {}", saved.getId(), item.getId());
        return bookingMapper.toResponseDto(saved);
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().equals(ownerId)) {
            throw new ForbiddenException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);
        log.info("Бронирование {} статус изменён на {}", bookingId, updated.getStatus());
        return bookingMapper.toResponseDto(updated);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new NotFoundException("Доступ запрещён");
        }

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Используем стратегию вместо switch-case
        List<Booking> bookings = stateContext.getBookingsByState(userId, state);
        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwner(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        // Используем стратегию для владельца
        List<Booking> bookings = stateContext.getBookingsByOwner(ownerId, state);
        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .toList();
    }
}
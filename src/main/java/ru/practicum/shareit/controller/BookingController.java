package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.BookingRequestDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.service.BookingService;
import ru.practicum.shareit.constants.HeadersConstants;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId,
            @Valid @RequestBody BookingRequestDto requestDto
    ) {
        log.info("POST /bookings - пользователь {} создаёт бронирование", userId);
        return bookingService.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId,
            @RequestParam Boolean approved
    ) {
        log.info("PATCH /bookings/{} - пользователь {} подтверждает бронирование: {}", bookingId, userId, approved);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId
    ) {
        log.info("GET /bookings/{} - пользователь {} получает бронирование", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUser(
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GET /bookings - пользователь {} получает бронирования со статусом {}", userId, state);
        return bookingService.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwner(
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("GET /bookings/owner - пользователь {} получает бронирования своих вещей со статусом {}", userId, state);
        return bookingService.getBookingsByOwner(userId, state);
    }
}
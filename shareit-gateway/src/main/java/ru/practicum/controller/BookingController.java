package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.BookingClient;
import ru.practicum.constants.HeaderConstants;
import ru.practicum.dto.BookingRequestDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @Valid @RequestBody BookingRequestDto bookingDto) {
        log.info("POST /bookings - userId={}", userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @PathVariable Long bookingId, @RequestParam Boolean approved) {
        log.info("PATCH /bookings/{} - userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @PathVariable Long bookingId) {
        log.info("GET /bookings/{} - userId={}", bookingId, userId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings - userId={}, state={}", userId, state);
        return bookingClient.getBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @RequestParam(defaultValue = "ALL") String state) {
        log.info("GET /bookings/owner - userId={}, state={}", userId, state);
        return bookingClient.getBookingsByOwner(userId, state);
    }
}
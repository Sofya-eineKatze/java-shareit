package ru.practicum.service;

import ru.practicum.dto.BookingCreateDto;
import ru.practicum.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto);
    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);
    BookingDto getBookingById(Long userId, Long bookingId);
    List<BookingDto> getBookingsByUser(Long userId, String state);
    List<BookingDto> getBookingsByOwner(Long userId, String state);
}
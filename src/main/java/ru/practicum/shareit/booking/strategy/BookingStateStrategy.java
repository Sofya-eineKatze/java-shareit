package ru.practicum.shareit.booking.strategy;

import ru.practicum.shareit.model.Booking;

import java.util.List;

public interface BookingStateStrategy {
    List<Booking> getBookings(Long userId);
}
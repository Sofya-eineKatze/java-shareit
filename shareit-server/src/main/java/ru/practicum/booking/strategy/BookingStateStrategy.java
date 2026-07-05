package ru.practicum.booking.strategy;

import ru.practicum.model.Booking;

import java.util.List;

public interface BookingStateStrategy {
    List<Booking> getBookings(Long userId);
}
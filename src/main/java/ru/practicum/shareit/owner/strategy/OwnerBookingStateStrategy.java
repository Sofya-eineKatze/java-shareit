package ru.practicum.shareit.owner.strategy;

import ru.practicum.shareit.model.Booking;

import java.util.List;

public interface OwnerBookingStateStrategy {
    List<Booking> getBookings(Long ownerId);
}
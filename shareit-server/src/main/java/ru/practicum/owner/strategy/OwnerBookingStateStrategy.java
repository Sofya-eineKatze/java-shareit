package ru.practicum.owner.strategy;

import ru.practicum.model.Booking;

import java.util.List;

public interface OwnerBookingStateStrategy {
    List<Booking> getOwnerBookings(Long ownerId);
}
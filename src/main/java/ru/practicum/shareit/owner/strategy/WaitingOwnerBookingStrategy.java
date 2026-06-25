package ru.practicum.shareit.owner.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.status.BookingStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingOwnerBookingStrategy implements OwnerBookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long ownerId) {
        return bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
    }
}
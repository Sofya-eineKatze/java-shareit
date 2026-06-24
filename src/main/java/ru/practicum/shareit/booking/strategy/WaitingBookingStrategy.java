package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.repository.BookingRepository;
import ru.practicum.shareit.status.BookingStatus;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WaitingBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
    }
}
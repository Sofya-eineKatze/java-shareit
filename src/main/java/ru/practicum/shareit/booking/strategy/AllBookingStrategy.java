package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AllBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findByBookerIdOrderByStartDesc(userId);
    }
}
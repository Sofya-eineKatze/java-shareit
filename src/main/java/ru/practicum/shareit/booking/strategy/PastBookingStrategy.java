package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component("pastBookingStrategy")
@RequiredArgsConstructor
public class PastBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
    }
}
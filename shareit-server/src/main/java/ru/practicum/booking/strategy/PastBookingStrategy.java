package ru.practicum.booking.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;
import ru.practicum.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component("PAST")
@RequiredArgsConstructor
@Slf4j
public class PastBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
    }
}
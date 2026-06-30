package ru.practicum.booking.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;
import ru.practicum.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component("FUTURE")
@RequiredArgsConstructor
@Slf4j
public class FutureBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
    }
}
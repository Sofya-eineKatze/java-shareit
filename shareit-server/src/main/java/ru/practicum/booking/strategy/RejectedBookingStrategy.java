package ru.practicum.booking.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;
import ru.practicum.repository.BookingRepository;
import ru.practicum.status.BookingStatus;

import java.util.List;

@Component("REJECTED")
@RequiredArgsConstructor
@Slf4j
public class RejectedBookingStrategy implements BookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getBookings(Long userId) {
        return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
    }
}
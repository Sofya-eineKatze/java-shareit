package ru.practicum.shareit.booking.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BookingStateContext {
    private final Map<String, BookingStateStrategy> strategies;

    public List<Booking> getBookingsByState(Long userId, String state) {
        BookingStateStrategy strategy = strategies.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return strategy.getBookings(userId);
    }
}
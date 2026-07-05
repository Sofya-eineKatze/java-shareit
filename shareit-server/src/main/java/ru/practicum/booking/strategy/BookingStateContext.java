package ru.practicum.booking.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingStateContext {
    private final Map<String, BookingStateStrategy> strategies;

    public List<Booking> getBookings(Long userId, String state) {
        BookingStateStrategy strategy = strategies.get(state.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return strategy.getBookings(userId);
    }
}
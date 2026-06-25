package ru.practicum.shareit.booking.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.owner.strategy.OwnerBookingStateStrategy;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BookingStateContext {
    private final Map<String, BookingStateStrategy> strategies;
    private final Map<String, OwnerBookingStateStrategy> ownerStrategies;

    public BookingStateContext(
            Map<String, BookingStateStrategy> strategies,
            Map<String, OwnerBookingStateStrategy> ownerStrategies
    ) {
        this.strategies = strategies;
        this.ownerStrategies = ownerStrategies;
        log.info("BookingStateContext initialized with strategies: {}", strategies.keySet());
        log.info("Owner strategies: {}", ownerStrategies.keySet());
    }

    public List<Booking> getBookingsByState(Long userId, String state) {
        String key = state.toUpperCase();
        BookingStateStrategy strategy = strategies.get(key);
        if (strategy == null) {
            log.error("Strategy not found for state: {}. Available: {}", state, strategies.keySet());
            throw new IllegalArgumentException("Unknown state: " + state + ". Available: " + strategies.keySet());
        }
        return strategy.getBookings(userId);
    }

    public List<Booking> getBookingsByOwner(Long ownerId, String state) {
        String key = state.toUpperCase();
        OwnerBookingStateStrategy strategy = ownerStrategies.get(key);
        if (strategy == null) {
            log.error("Owner strategy not found for state: {}. Available: {}", state, ownerStrategies.keySet());
            throw new IllegalArgumentException("Unknown state: " + state + ". Available: " + ownerStrategies.keySet());
        }
        return strategy.getBookings(ownerId);
    }
}
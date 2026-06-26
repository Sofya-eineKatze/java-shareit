package ru.practicum.shareit.booking.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
            @Qualifier("allBookingStrategy") BookingStateStrategy all,
            @Qualifier("currentBookingStrategy") BookingStateStrategy current,
            @Qualifier("pastBookingStrategy") BookingStateStrategy past,
            @Qualifier("futureBookingStrategy") BookingStateStrategy future,
            @Qualifier("waitingBookingStrategy") BookingStateStrategy waiting,
            @Qualifier("rejectedBookingStrategy") BookingStateStrategy rejected,
            Map<String, OwnerBookingStateStrategy> ownerStrategies
    ) {
        this.strategies = Map.of(
                "ALL", all,
                "CURRENT", current,
                "PAST", past,
                "FUTURE", future,
                "WAITING", waiting,
                "REJECTED", rejected
        );
        this.ownerStrategies = ownerStrategies;
        log.info("BookingStateContext strategies: {}", this.strategies.keySet());
        log.info("Owner strategies: {}", this.ownerStrategies.keySet());
    }

    public List<Booking> getBookingsByState(Long userId, String state) {
        String key = state.toUpperCase();
        BookingStateStrategy strategy = strategies.get(key);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown state: " + state + ". Available: " + strategies.keySet());
        }
        return strategy.getBookings(userId);
    }

    public List<Booking> getBookingsByOwner(Long ownerId, String state) {
        String key = state.toUpperCase();
        OwnerBookingStateStrategy strategy = ownerStrategies.get(key);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown state: " + state + ". Available: " + ownerStrategies.keySet());
        }
        return strategy.getBookings(ownerId);
    }
}
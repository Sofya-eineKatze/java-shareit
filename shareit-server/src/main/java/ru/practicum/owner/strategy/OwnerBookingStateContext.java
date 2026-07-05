package ru.practicum.owner.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerBookingStateContext {
    private final Map<String, OwnerBookingStateStrategy> strategies;

    public List<Booking> getOwnerBookings(Long ownerId, String state) {
        OwnerBookingStateStrategy strategy = strategies.get(state.toUpperCase() + "_OWNER");
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return strategy.getOwnerBookings(ownerId);
    }
}
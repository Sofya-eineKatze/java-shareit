package ru.practicum.owner.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;
import ru.practicum.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component("CURRENT_OWNER")
@RequiredArgsConstructor
@Slf4j
public class CurrentOwnerBookingStrategy implements OwnerBookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getOwnerBookings(Long ownerId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findAllByOwnerIdAndCurrent(ownerId, now);
    }
}
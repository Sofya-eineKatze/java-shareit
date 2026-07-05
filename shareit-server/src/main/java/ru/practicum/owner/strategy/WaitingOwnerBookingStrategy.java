package ru.practicum.owner.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.model.Booking;
import ru.practicum.repository.BookingRepository;
import ru.practicum.status.BookingStatus;

import java.util.List;

@Component("WAITING_OWNER")
@RequiredArgsConstructor
@Slf4j
public class WaitingOwnerBookingStrategy implements OwnerBookingStateStrategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> getOwnerBookings(Long ownerId) {
        return bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING);
    }
}
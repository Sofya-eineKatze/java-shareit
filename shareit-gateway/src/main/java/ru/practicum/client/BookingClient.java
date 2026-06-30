package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.BookingRequestDto;

@Service
public class BookingClient extends BaseClient {
    public BookingClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingRequestDto bookingDto) {
        return post("/bookings", userId, bookingDto);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        return patch("/bookings/" + bookingId + "?approved=" + approved, userId, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, userId);
    }

    public ResponseEntity<Object> getBookingsByUser(Long userId, String state) {
        return get("/bookings?state=" + state, userId);
    }

    public ResponseEntity<Object> getBookingsByOwner(Long userId, String state) {
        return get("/bookings/owner?state=" + state, userId);
    }
}
package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.BookingDto;
import ru.practicum.model.Booking;
import ru.practicum.model.Item;
import ru.practicum.model.User;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    @Test
    void toDto_ShouldMapAllFieldsWithNestedItemAndBooker() {
        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        User booker = new User();
        booker.setId(5L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2026, 7, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2026, 7, 2, 10, 0));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 7, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 7, 2, 10, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
        assertThat(dto.getBooker().getId()).isEqualTo(5L);
    }

    @Test
    void toDto_NullBooking_ShouldReturnNull() {
        BookingDto dto = bookingMapper.toDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void toDto_NullItem_ShouldHaveNullItemInfo() {
        User booker = new User();
        booker.setId(5L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(null);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto.getItem()).isNull();
        assertThat(dto.getBooker()).isNotNull();
    }

    @Test
    void toDto_NullBooker_ShouldHaveNullBookerInfo() {
        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(null);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto dto = bookingMapper.toDto(booking);

        assertThat(dto.getBooker()).isNull();
        assertThat(dto.getItem()).isNotNull();
    }
}
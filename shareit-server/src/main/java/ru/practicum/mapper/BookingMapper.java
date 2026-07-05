package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.BookingDto;
import ru.practicum.model.Booking;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        if (booking.getItem() != null) {
            dto.setItem(new BookingDto.ItemInfo(booking.getItem().getId(), booking.getItem().getName()));
        }
        if (booking.getBooker() != null) {
            dto.setBooker(new BookingDto.BookerInfo(booking.getBooker().getId()));
        }
        return dto;
    }
}
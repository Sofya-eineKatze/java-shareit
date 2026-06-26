package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.BookingRequestDto;
import ru.practicum.shareit.dto.BookingResponseDto;
import ru.practicum.shareit.model.Booking;
import ru.practicum.shareit.status.BookingStatus;

@Component
public class BookingMapper {
    public Booking toEntity(BookingRequestDto dto, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setStatus(status);
        return booking;
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) return null;
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus()
        );
    }

    public BookingResponseDto toResponseDto(Booking booking) {
        if (booking == null) return null;
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(new BookingResponseDto.BookerDto(
                booking.getBooker().getId(),
                booking.getBooker().getName()
        ));
        dto.setItem(new BookingResponseDto.ItemDto(
                booking.getItem().getId(),
                booking.getItem().getName()
        ));
        return dto;
    }
}
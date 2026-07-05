package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.BookingCreateDto;
import ru.practicum.dto.BookingDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.BookingMapper;
import ru.practicum.model.Booking;
import ru.practicum.model.Item;
import ru.practicum.model.User;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.status.BookingStatus;
import ru.practicum.booking.strategy.BookingStateContext;
import ru.practicum.owner.strategy.OwnerBookingStateContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private BookingStateContext bookingStateContext;
    @Mock
    private OwnerBookingStateContext ownerBookingStateContext;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingCreateDto bookingCreateDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "User", "user@email.com");
        owner = new User(2L, "Owner", "owner@email.com");
        item = new Item(1L, "Дрель", "Мощная дрель", true, 2L, null);

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
        bookingCreateDto = new BookingCreateDto(1L, start, end);
        bookingDto = new BookingDto(1L, start, end, BookingStatus.WAITING,
                new BookingDto.BookerInfo(1L), new BookingDto.ItemInfo(1L, "Дрель"));
    }

    @Test
    void createBooking_ShouldCreateBooking() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(1L, bookingCreateDto);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ItemNotAvailable_ShouldThrowException() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
    }

    @Test
    void createBooking_UserIsOwner_ShouldThrowException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(2L, bookingCreateDto));
    }

    @Test
    void createBooking_ItemNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        BookingCreateDto dto = new BookingCreateDto(999L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(1L, dto));
    }

    @Test
    void createBooking_StartAfterEnd_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingCreateDto invalidDto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, invalidDto));
    }

    @Test
    void createBooking_StartInPast_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        BookingCreateDto invalidDto = new BookingCreateDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, invalidDto));
    }

    @Test
    void updateBooking_ShouldApproveBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.updateBooking(2L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void updateBooking_RejectBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.updateBooking(2L, 1L, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void updateBooking_WrongUser_ShouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.updateBooking(999L, 1L, true));
    }

    @Test
    void updateBooking_AlreadyApproved_ShouldThrowException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateBooking(2L, 1L, true));
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void getBookingById_WrongUser_ShouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(999L, 1L));
    }

    @Test
    void getBookingsByUser_ShouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingStateContext.getBookings(1L, "ALL")).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsByUser(1L, "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getBookingsByOwner_ShouldReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(ownerBookingStateContext.getOwnerBookings(2L, "ALL")).thenReturn(List.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingService.getBookingsByOwner(2L, "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
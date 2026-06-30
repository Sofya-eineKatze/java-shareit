package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.BookingCreateDto;
import ru.practicum.dto.BookingDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Booking;
import ru.practicum.model.Item;
import ru.practicum.model.User;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item = itemRepository.save(item);
    }

    @Test
    void createBooking_ValidRequest_ShouldCreateBookingWithWaitingStatus() {
        BookingCreateDto createDto = new BookingCreateDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        BookingDto result = bookingService.createBooking(booker.getId(), createDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(result.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(result.getItem().getId()).isEqualTo(item.getId());
    }

    @Test
    void createBooking_ItemNotAvailable_ShouldThrowValidationException() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingCreateDto createDto = new BookingCreateDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(booker.getId(), createDto));
    }

    @Test
    void createBooking_OwnerBooksOwnItem_ShouldThrowNotFoundException() {
        BookingCreateDto createDto = new BookingCreateDto(
                item.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(owner.getId(), createDto));
    }

    @Test
    void getBookingsByUser_StateAll_ShouldReturnAllBookings() {
        saveBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
        saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "ALL");

        assertThat(result).hasSize(2);
    }

    @Test
    void getBookingsByUser_StatePast_ShouldReturnOnlyPastBookings() {
        Booking past = saveBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
        saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "PAST");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(past.getId());
    }

    @Test
    void getBookingsByUser_StateFuture_ShouldReturnOnlyFutureBookings() {
        saveBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
        Booking future = saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "FUTURE");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(future.getId());
    }

    @Test
    void getBookingsByUser_StateWaiting_ShouldReturnOnlyWaitingBookings() {
        saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);
        saveBooking(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "WAITING");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByUser_StateRejected_ShouldReturnOnlyRejectedBookings() {
        saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.REJECTED);
        saveBooking(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), BookingStatus.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByUser(booker.getId(), "REJECTED");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void getBookingsByOwner_StateAll_ShouldReturnAllBookingsForOwnerItems() {
        saveBooking(LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), BookingStatus.APPROVED);
        saveBooking(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), BookingStatus.WAITING);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "ALL");

        assertThat(result).hasSize(2);
    }

    @Test
    void getBookingsByOwner_StateCurrent_ShouldReturnOngoingBookings() {
        Booking current = saveBooking(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(2), BookingStatus.APPROVED);

        List<BookingDto> result = bookingService.getBookingsByOwner(owner.getId(), "CURRENT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(current.getId());
    }

    private Booking saveBooking(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ItemDto;
import ru.practicum.model.Booking;
import ru.practicum.model.Comment;
import ru.practicum.model.Item;
import ru.practicum.model.User;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

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
    void getItemsByOwner_ShouldReturnItemsWithLastAndNextBookingAndComments() {
        Booking pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStart(LocalDateTime.now().minusDays(5));
        pastBooking.setEnd(LocalDateTime.now().minusDays(4));
        pastBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(pastBooking);

        Booking futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(LocalDateTime.now().plusDays(3));
        futureBooking.setEnd(LocalDateTime.now().plusDays(4));
        futureBooking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(futureBooking);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(booker);
        comment.setText("Отличная вещь!");
        comment.setCreated(LocalDateTime.now());
        commentRepository.save(comment);

        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(1);
        ItemDto dto = result.get(0);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getLastBooking().getId()).isEqualTo(pastBooking.getId());
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(futureBooking.getId());
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Отличная вещь!");
    }

    @Test
    void getItemsByOwner_NoBookingsOrComments_ShouldReturnItemWithEmptyLists() {
        List<ItemDto> result = itemService.getItemsByOwner(owner.getId());

        assertThat(result).hasSize(1);
        ItemDto dto = result.get(0);
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isEmpty();
    }

    @Test
    void getItemsByOwner_OwnerHasNoItems_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.getItemsByOwner(booker.getId());

        assertThat(result).isEmpty();
    }
}
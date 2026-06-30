package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.JsonTestConfig;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JsonTestConfig.class)
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize_WithCommentsAndBookings() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        dto.setRequestId(5L);

        BookingDto lastBooking = new BookingDto();
        lastBooking.setId(10L);
        lastBooking.setStart(LocalDateTime.of(2026, 6, 1, 10, 0));
        lastBooking.setEnd(LocalDateTime.of(2026, 6, 2, 10, 0));
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setBooker(new BookingDto.BookerInfo(2L));
        dto.setLastBooking(lastBooking);

        BookingDto nextBooking = new BookingDto();
        nextBooking.setId(11L);
        nextBooking.setStart(LocalDateTime.of(2026, 7, 1, 10, 0));
        nextBooking.setEnd(LocalDateTime.of(2026, 7, 2, 10, 0));
        nextBooking.setStatus(BookingStatus.WAITING);
        nextBooking.setBooker(new BookingDto.BookerInfo(3L));
        dto.setNextBooking(nextBooking);

        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Отличная вещь!");
        comment.setAuthorName("Alice");
        comment.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));
        dto.setComments(List.of(comment));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Отличная вещь!");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Alice");
    }

    @Test
    void testSerialize_WithoutBookingsAndComments() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(2L);
        dto.setName("Лестница");
        dto.setDescription("Стремянка");
        dto.setAvailable(true);
        dto.setComments(List.of());

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).hasJsonPathValue("$.comments");
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEmpty();
        assertThat(result).hasEmptyJsonPathValue("$.lastBooking");
        assertThat(result).hasEmptyJsonPathValue("$.nextBooking");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\":1,"
                + "\"name\":\"Дрель\","
                + "\"description\":\"Мощная дрель\","
                + "\"available\":true,"
                + "\"requestId\":5,"
                + "\"comments\":["
                + "{\"id\":1,\"text\":\"Отлично\",\"authorName\":\"Bob\",\"created\":\"2026-05-01T12:00:00\"}"
                + "]"
                + "}";

        ItemDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getRequestId()).isEqualTo(5L);
        assertThat(dto.getComments()).hasSize(1);
        assertThat(dto.getComments().get(0).getText()).isEqualTo("Отлично");
        assertThat(dto.getComments().get(0).getAuthorName()).isEqualTo("Bob");
    }
}
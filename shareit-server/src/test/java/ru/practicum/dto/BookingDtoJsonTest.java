package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.JsonTestConfig;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JsonTestConfig.class)
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2026, 7, 1, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 7, 2, 10, 0));
        dto.setStatus(BookingStatus.WAITING);
        dto.setBooker(new BookingDto.BookerInfo(5L));
        dto.setItem(new BookingDto.ItemInfo(10L, "Дрель"));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-07-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-07-02T10:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\":1,"
                + "\"start\":\"2026-07-01T10:00:00\","
                + "\"end\":\"2026-07-02T10:00:00\","
                + "\"status\":\"APPROVED\","
                + "\"booker\":{\"id\":5},"
                + "\"item\":{\"id\":10,\"name\":\"Дрель\"}"
                + "}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 7, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 7, 2, 10, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
        assertThat(dto.getBooker().getId()).isEqualTo(5L);
        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
    }
}
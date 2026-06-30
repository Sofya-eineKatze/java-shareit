package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.JsonTestConfig;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JsonTestConfig.class)
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto dto = new CommentDto();
        dto.setId(1L);
        dto.setText("Отличная вещь!");
        dto.setAuthorName("Alice");
        dto.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Alice");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-05-01T12:00:00");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\":1,"
                + "\"text\":\"Отличная вещь!\","
                + "\"authorName\":\"Alice\","
                + "\"created\":\"2026-05-01T12:00:00\""
                + "}";

        CommentDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Отличная вещь!");
        assertThat(dto.getAuthorName()).isEqualTo("Alice");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 5, 1, 12, 0));
    }
}
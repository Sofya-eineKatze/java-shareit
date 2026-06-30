package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.JsonTestConfig;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JsonTestConfig.class)
class CommentRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CommentRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentRequestDto dto = new CommentRequestDto("Отличная вещь!");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная вещь!");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"text\":\"Отличная вещь!\"}";

        CommentRequestDto dto = json.parseObject(content);

        assertThat(dto.getText()).isEqualTo("Отличная вещь!");
    }
}
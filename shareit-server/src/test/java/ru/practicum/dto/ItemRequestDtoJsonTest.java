package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.JsonTestConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = JsonTestConfig.class)
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testSerialize_WithItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));
        dto.setItems(List.of(item));

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-05-01T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
    }

    @Test
    void testSerialize_WithoutItems() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(2L);
        dto.setDescription("Нужна лестница");
        dto.setCreated(LocalDateTime.of(2026, 5, 2, 12, 0));
        dto.setItems(List.of());

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{"
                + "\"id\":1,"
                + "\"description\":\"Нужна дрель\","
                + "\"created\":\"2026-05-01T12:00:00\","
                + "\"items\":["
                + "{\"id\":1,\"name\":\"Дрель\",\"description\":\"Мощная дрель\",\"available\":true}"
                + "]"
                + "}";

        ItemRequestDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getCreated()).isEqualTo(LocalDateTime.of(2026, 5, 1, 12, 0));
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
    }
}
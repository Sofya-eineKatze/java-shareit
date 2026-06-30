package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.ItemDto;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    void toDto_WithoutRequest_ShouldMapBasicFields() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(10L);
        item.setRequest(null);

        ItemDto dto = itemMapper.toDto(item);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель");
        assertThat(dto.getDescription()).isEqualTo("Мощная дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isNull();
    }

    @Test
    void toDto_WithRequest_ShouldMapRequestId() {
        ItemRequest request = new ItemRequest();
        request.setId(5L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        item.setOwner(10L);
        item.setRequest(request);

        ItemDto dto = itemMapper.toDto(item);

        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void toDto_NullItem_ShouldReturnNull() {
        ItemDto dto = itemMapper.toDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void toEntity_ShouldMapAllFieldsIncludingOwnerAndRequest() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        ItemRequest request = new ItemRequest();
        request.setId(5L);

        Item item = itemMapper.toEntity(dto, 10L, request);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Мощная дрель");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(10L);
        assertThat(item.getRequest()).isEqualTo(request);
    }

    @Test
    void toEntity_WithoutRequest_ShouldHaveNullRequest() {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        Item item = itemMapper.toEntity(dto, 10L, null);

        assertThat(item.getRequest()).isNull();
    }

    @Test
    void toEntity_NullDto_ShouldReturnNull() {
        Item item = itemMapper.toEntity(null, 10L, null);

        assertThat(item).isNull();
    }
}
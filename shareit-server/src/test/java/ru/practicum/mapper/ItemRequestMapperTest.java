package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.model.ItemRequest;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toDto_Single_ShouldReturnDtoWithEmptyItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));
        request.setRequestor(new User(1L, "Alice", "alice@example.com"));

        ItemRequestDto dto = itemRequestMapper.toDto(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toDto_Single_NullRequest_ShouldReturnNull() {
        ItemRequestDto dto = itemRequestMapper.toDto((ItemRequest) null);

        assertThat(dto).isNull();
    }

    @Test
    void toDto_BatchMap_ShouldUseProvidedMapWithoutQueryingRepository() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));

        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Дрель");

        Map<Long, List<ItemDto>> itemsByRequestId = Map.of(1L, List.of(itemDto));

        ItemRequestDto dto = itemRequestMapper.toDto(request, itemsByRequestId);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
    }

    @Test
    void toDto_BatchMap_RequestNotInMap_ShouldReturnEmptyList() {
        ItemRequest request = new ItemRequest();
        request.setId(3L);
        request.setDescription("Нужен молоток");
        request.setCreated(LocalDateTime.of(2026, 5, 3, 12, 0));

        Map<Long, List<ItemDto>> itemsByRequestId = Map.of(1L, List.of());

        ItemRequestDto dto = itemRequestMapper.toDto(request, itemsByRequestId);

        assertThat(dto.getItems()).isEmpty();
    }

    @Test
    void toDto_BatchMap_NullRequest_ShouldReturnNull() {
        ItemRequestDto dto = itemRequestMapper.toDto(null, Map.of());

        assertThat(dto).isNull();
    }

    @Test
    void toDto_Single_NoItems_ShouldReturnEmptyList() {
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        request.setDescription("Нужна лестница");
        request.setCreated(LocalDateTime.of(2026, 5, 2, 12, 0));

        ItemRequestDto dto = itemRequestMapper.toDto(request);

        assertThat(dto.getItems()).isEmpty();
    }
}
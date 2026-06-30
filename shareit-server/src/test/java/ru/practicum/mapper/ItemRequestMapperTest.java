package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;
import ru.practicum.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @Test
    void toDto_Single_ShouldQueryRepositoryAndMapItems() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Нужна дрель");
        request.setCreated(LocalDateTime.of(2026, 5, 1, 12, 0));

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");

        ItemDto itemDto = new ItemDto();
        itemDto.setId(10L);
        itemDto.setName("Дрель");

        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemRequestDto dto = itemRequestMapper.toDto(request);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Дрель");
        verify(itemRepository).findByRequestId(1L);
    }

    @Test
    void toDto_Single_NullRequest_ShouldReturnNull() {
        ItemRequestDto dto = itemRequestMapper.toDto((ItemRequest) null);

        assertThat(dto).isNull();
    }

    @Test
    void toDto_Single_NoItems_ShouldReturnEmptyList() {
        ItemRequest request = new ItemRequest();
        request.setId(2L);
        request.setDescription("Нужна лестница");
        request.setCreated(LocalDateTime.of(2026, 5, 2, 12, 0));

        when(itemRepository.findByRequestId(2L)).thenReturn(List.of());

        ItemRequestDto dto = itemRequestMapper.toDto(request);

        assertThat(dto.getItems()).isEmpty();
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
        verify(itemRepository, org.mockito.Mockito.never()).findByRequestId(eq(1L));
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
}
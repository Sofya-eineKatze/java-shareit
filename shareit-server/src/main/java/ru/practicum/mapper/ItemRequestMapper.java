package ru.practicum.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;
import ru.practicum.repository.ItemRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;
        List<Item> items = itemRepository.findByRequestId(request.getId());
        return toDto(request, items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList()));
    }

    public ItemRequestDto toDto(ItemRequest request, Map<Long, List<ItemDto>> itemsByRequestId) {
        if (request == null) return null;
        List<ItemDto> items = itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList());
        return toDto(request, items);
    }

    private ItemRequestDto toDto(ItemRequest request, List<ItemDto> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(items);
        return dto;
    }
}
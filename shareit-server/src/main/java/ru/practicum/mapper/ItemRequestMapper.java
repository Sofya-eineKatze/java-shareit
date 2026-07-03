package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.model.ItemRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest request) {
        if (request == null) return null;
        return toDto(request, Collections.emptyList());
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
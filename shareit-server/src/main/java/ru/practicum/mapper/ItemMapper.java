package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.ItemDto;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }

    public Item toEntity(ItemDto dto, Long ownerId, ItemRequest request) {
        if (dto == null) return null;
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(ownerId);
        item.setRequest(request); // вот чего не хватало
        return item;
    }
}
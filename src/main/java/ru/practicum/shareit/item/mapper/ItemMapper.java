package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        if (item == null) return null;
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
        );
    }

    public Item toEntity(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) return null;
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId
        );
    }

    public void updateEntity(Item existing, ItemDto source) {
        if (source.getName() != null) {
            existing.setName(source.getName());
        }
        if (source.getDescription() != null) {
            existing.setDescription(source.getDescription());
        }
        if (source.getAvailable() != null) {
            existing.setAvailable(source.getAvailable());
        }
    }
}
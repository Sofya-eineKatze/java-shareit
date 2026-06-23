package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    private final CommentMapper commentMapper;

    public ItemDto toDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public ItemDto toDto(Item item, List<Comment> comments) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (comments != null && !comments.isEmpty()) {
            dto.setComments(comments.stream()
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Item toEntity(ItemDto itemDto, Long ownerId) {
        if (itemDto == null) return null;
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(ownerId);
        return item;
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
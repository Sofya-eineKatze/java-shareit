package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long ownerId);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto createItem(Long ownerId, ItemDto itemDto);

    ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long itemId, Long userId, String text);  // ← добавить
}
package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long ownerId);
    ItemDto getItemById(Long id, Long userId);
    ItemDto createItem(Long ownerId, ItemDto itemDto);
    ItemDto updateItem(Long itemId, Long ownerId, ItemDto itemDto);
    List<ItemDto> searchItems(String text);
}
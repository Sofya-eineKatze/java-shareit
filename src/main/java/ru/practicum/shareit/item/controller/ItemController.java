package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.HeadersConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader(HeadersConstants.USER_ID_HEADER) Long ownerId) {
        log.info("GET /items - получение всех вещей пользователя {}", ownerId);
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable Long itemId,
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long userId
    ) {
        log.info("GET /items/{} - получение вещи пользователем {}", itemId, userId);
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto createItem(
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long ownerId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("POST /items - создание вещи для пользователя {}", ownerId);
        return itemService.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @PathVariable Long itemId,
            @RequestHeader(HeadersConstants.USER_ID_HEADER) Long ownerId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем {}", itemId, ownerId);
        return itemService.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("GET /items/search - поиск вещей по тексту '{}'", text);
        return itemService.searchItems(text);
    }
}
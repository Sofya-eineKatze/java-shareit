package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemClient;
import ru.practicum.constants.HeaderConstants;
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByOwner(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId) {
        log.info("GET /items - ownerId={}", ownerId);
        return itemClient.getItemsByOwner(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId, @RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId) {
        log.info("GET /items/{} - userId={}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId, @Valid @RequestBody ItemDto itemDto) {
        log.info("POST /items - ownerId={}", ownerId);
        return itemClient.createItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId, @RequestHeader(HeaderConstants.USER_ID_HEADER) Long ownerId, @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} - ownerId={}", itemId, ownerId);
        return itemClient.updateItem(itemId, ownerId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("GET /items/search - text={}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId, @RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("POST /items/{}/comment - userId={}", itemId, userId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
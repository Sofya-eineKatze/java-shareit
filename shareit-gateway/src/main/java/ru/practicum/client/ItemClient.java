package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CommentRequestDto;
import ru.practicum.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    public ItemClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> getItemsByOwner(Long ownerId) {
        return get("/items", ownerId);
    }

    public ResponseEntity<Object> getItemById(Long itemId, Long userId) {
        return get("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(Long ownerId, ItemDto itemDto) {
        return post("/items", ownerId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long ownerId, ItemDto itemDto) {
        return patch("/items/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> searchItems(String text) {
        return get("/items/search?text=" + text);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long userId, CommentRequestDto commentDto) {
        return post("/items/" + itemId + "/comment", userId, commentDto);
    }
}
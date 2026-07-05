package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        return post("/requests", userId, requestDto);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, Integer from, Integer size) {
        return get("/requests/all?from=" + from + "&size=" + size, userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/requests/" + requestId, userId);
    }
}
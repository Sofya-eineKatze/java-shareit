package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ItemRequestClient;
import ru.practicum.constants.HeaderConstants;
import ru.practicum.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("POST /requests - userId={}", userId);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId) {
        log.info("GET /requests - userId={}", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from не может быть отрицательным") Integer from,
            @RequestParam(defaultValue = "10") @Positive(message = "size должен быть больше нуля") Integer size) {
        log.info("GET /requests/all - userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HeaderConstants.USER_ID_HEADER) Long userId, @PathVariable Long requestId) {
        log.info("GET /requests/{} - userId={}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
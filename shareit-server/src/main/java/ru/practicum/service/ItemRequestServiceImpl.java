package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.mapper.ItemRequestMapper;
import ru.practicum.model.Item;
import ru.practicum.model.ItemRequest;
import ru.practicum.model.User;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestCreateDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(createDto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        log.info("Создан запрос с id {} для пользователя {}", saved.getId(), userId);
        return mapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> requests = requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        return mapRequestsWithItems(requests);
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        Map<Long, List<ItemDto>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(itemMapper::toDto, Collectors.toList())
                ));

        return mapper.toDto(request, itemsByRequestId);
    }

    private List<ItemRequestDto> mapRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        Map<Long, List<ItemDto>> itemsByRequestId = itemRepository.findByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(itemMapper::toDto, Collectors.toList())
                ));

        return requests.stream()
                .map(request -> mapper.toDto(request, itemsByRequestId))
                .collect(Collectors.toList());
    }
}
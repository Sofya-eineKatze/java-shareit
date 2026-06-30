package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.ItemDto;
import ru.practicum.dto.ItemRequestCreateDto;
import ru.practicum.dto.ItemRequestDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.mapper.ItemRequestMapper;
import ru.practicum.model.ItemRequest;
import ru.practicum.model.User;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestMapper mapper;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private User user;
    private ItemRequest request;
    private ItemRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@email.com");
        request = new ItemRequest(1L, "Нужна дрель", user, LocalDateTime.now());
        createDto = new ItemRequestCreateDto("Нужна дрель");
    }

    @Test
    void createRequest_ShouldCreateRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);
        when(mapper.toDto(any(ItemRequest.class))).thenReturn(new ItemRequestDto());

        ItemRequestDto result = requestService.createRequest(1L, createDto);

        assertNotNull(result);
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.createRequest(999L, createDto));
    }

    @Test
    void getUserRequests_ShouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(1L))).thenReturn(List.of());
        when(mapper.toDto(eq(request), any(Map.class))).thenReturn(new ItemRequestDto());

        List<ItemRequestDto> result = requestService.getUserRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRequests_ShouldReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any(PageRequest.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findByRequestIdIn(List.of(1L))).thenReturn(List.of());
        when(mapper.toDto(eq(request), any(Map.class))).thenReturn(new ItemRequestDto());

        List<ItemRequestDto> result = requestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequestById_ShouldReturnRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(mapper.toDto(any(ItemRequest.class))).thenReturn(new ItemRequestDto());

        ItemRequestDto result = requestService.getRequestById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void getRequestById_NotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 999L));
    }
}
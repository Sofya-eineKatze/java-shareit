package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.ItemMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Item;
import ru.practicum.model.User;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@email.com");
        item = new Item(1L, "Дрель", "Мощная дрель", true, 1L, null);
        itemDto = new ItemDto(1L, "Дрель", "Мощная дрель", true, null, null, null, null);
    }

    @Test
    void getItemsByOwner_ShouldReturnItems() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findByOwner(1L)).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsByOwner(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Дрель", result.get(0).getName());
    }

    @Test
    void getItemById_ShouldReturnItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getItemById_NotFound_ShouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(999L, 1L));
    }

    @Test
    void createItem_ShouldCreateItem() {
        ItemDto inputDto = new ItemDto(null, "Новая вещь", "Описание", true, null, null, null, null);
        Item newItem = new Item(null, "Новая вещь", "Описание", true, 1L, null);
        Item savedItem = new Item(2L, "Новая вещь", "Описание", true, 1L, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.toEntity(inputDto, 1L, null)).thenReturn(newItem);
        when(itemRepository.save(newItem)).thenReturn(savedItem);
        when(itemMapper.toDto(savedItem)).thenReturn(new ItemDto(2L, "Новая вещь", "Описание", true, null, null, null, null));

        ItemDto result = itemService.createItem(1L, inputDto);

        assertNotNull(result);
        assertEquals(2L, result.getId());
    }

    @Test
    void createItem_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(999L, itemDto));
    }

    @Test
    void updateItem_ShouldUpdateItem() {
        ItemDto updateDto = new ItemDto(null, "Обновленная дрель", null, null, null, null, null, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(new ItemDto(1L, "Обновленная дрель", "Мощная дрель", true, null, null, null, null));

        ItemDto result = itemService.updateItem(1L, 1L, updateDto);

        assertNotNull(result);
        assertEquals("Обновленная дрель", result.getName());
    }

    @Test
    void updateItem_WrongOwner_ShouldThrowException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(1L, 999L, itemDto));
    }

    @Test
    void searchItems_ShouldReturnItems() {
        when(itemRepository.searchByText("дрель")).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchItems("дрель");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addComment_ShouldCreateComment() {
        CommentCreateDto commentRequest = new CommentCreateDto("Отличная вещь!");
        Comment comment = new Comment(1L, "Отличная вещь!", item, user, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1L, "Отличная вещь!", "Test User", LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(1L, 1L, commentRequest);

        assertNotNull(result);
        assertEquals("Отличная вещь!", result.getText());
    }

    @Test
    void addComment_NoBooking_ShouldThrowException() {
        CommentCreateDto commentRequest = new CommentCreateDto("Отличная вещь!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, commentRequest));
    }

    @Test
    void searchItems_EmptyText_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.searchItems("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepository);
    }
}
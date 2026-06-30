package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.CommentCreateDto;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.ItemDto;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private CommentRepository commentRepository;

    private ItemDto buildItemDto(Long id, String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    @Test
    void getItemsByOwner_ShouldReturnList() throws Exception {
        ItemDto item1 = buildItemDto(1L, "Дрель", "Мощная дрель", true);
        ItemDto item2 = buildItemDto(2L, "Лестница", "Стремянка", true);
        when(itemService.getItemsByOwner(1L)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Дрель"))
                .andExpect(jsonPath("$[1].name").value("Лестница"));
    }

    @Test
    void getItemById_ShouldReturnItem() throws Exception {
        ItemDto item = buildItemDto(1L, "Дрель", "Мощная дрель", true);
        when(itemService.getItemById(1L, 1L)).thenReturn(item);

        mockMvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        ItemDto inputDto = buildItemDto(null, "Дрель", "Мощная дрель", true);
        ItemDto savedDto = buildItemDto(1L, "Дрель", "Мощная дрель", true);
        when(itemService.createItem(eq(1L), any(ItemDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto updateDto = buildItemDto(null, "Дрель новая", null, null);
        ItemDto resultDto = buildItemDto(1L, "Дрель новая", "Мощная дрель", true);
        when(itemService.updateItem(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(resultDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Дрель новая"));
    }

    @Test
    void searchItems_ShouldReturnList() throws Exception {
        ItemDto item = buildItemDto(1L, "Дрель", "Мощная дрель", true);
        when(itemService.searchItems("дрель")).thenReturn(List.of(item));

        mockMvc.perform(get("/items/search").param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        CommentCreateDto inputDto = new CommentCreateDto("Отличная вещь!");
        CommentDto savedDto = new CommentDto(1L, "Отличная вещь!", "Alice", LocalDateTime.now());
        when(itemService.addComment(eq(1L), eq(1L), any(CommentCreateDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная вещь!"))
                .andExpect(jsonPath("$.authorName").value("Alice"));
    }
}
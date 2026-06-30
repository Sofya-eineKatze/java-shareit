package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.BookingCreateDto;
import ru.practicum.dto.BookingDto;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.ItemRepository;
import ru.practicum.repository.ItemRequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.BookingService;
import ru.practicum.status.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

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

    private BookingDto buildBookingDto(Long id, BookingStatus status) {
        BookingDto dto = new BookingDto();
        dto.setId(id);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        dto.setStatus(status);
        dto.setBooker(new BookingDto.BookerInfo(1L));
        dto.setItem(new BookingDto.ItemInfo(1L, "Дрель"));
        return dto;
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        BookingCreateDto inputDto = new BookingCreateDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto savedDto = buildBookingDto(1L, BookingStatus.WAITING);
        when(bookingService.createBooking(eq(1L), any(BookingCreateDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"));
    }

    @Test
    void updateBooking_ShouldReturnApprovedBooking() throws Exception {
        BookingDto resultDto = buildBookingDto(1L, BookingStatus.APPROVED);
        when(bookingService.updateBooking(1L, 1L, true)).thenReturn(resultDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        BookingDto dto = buildBookingDto(1L, BookingStatus.WAITING);
        when(bookingService.getBookingById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBookingsByUser_ShouldReturnList() throws Exception {
        BookingDto dto = buildBookingDto(1L, BookingStatus.WAITING);
        when(bookingService.getBookingsByUser(1L, "ALL")).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBookingsByOwner_ShouldReturnList() throws Exception {
        BookingDto dto = buildBookingDto(1L, BookingStatus.WAITING);
        when(bookingService.getBookingsByOwner(1L, "ALL")).thenReturn(List.of(dto));

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
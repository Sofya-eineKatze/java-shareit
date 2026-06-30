package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.client.BookingClient;
import ru.practicum.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
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
    private BookingClient bookingClient;

    @Test
    void createBooking_ValidDto_ShouldDelegateToClient() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(bookingClient.createBooking(eq(1L), any(BookingRequestDto.class))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_MissingItemId_ShouldReturn400() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_StartInPast_ShouldReturn400() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_EndInPast_ShouldReturn400() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_StartAfterEnd_ShouldReturn400() throws Exception {
        BookingRequestDto dto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookingClient);
    }

    @Test
    void updateBooking_ShouldDelegateToClient() throws Exception {
        when(bookingClient.updateBooking(1L, 1L, true)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingById_ShouldDelegateToClient() throws Exception {
        when(bookingClient.getBookingById(1L, 1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByUser_ShouldDelegateToClient() throws Exception {
        when(bookingClient.getBookingsByUser(1L, "ALL")).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsByOwner_ShouldDelegateToClient() throws Exception {
        when(bookingClient.getBookingsByOwner(1L, "ALL")).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1L).param("state", "ALL"))
                .andExpect(status().isOk());
    }
}
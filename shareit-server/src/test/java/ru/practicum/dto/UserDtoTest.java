package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    @Test
    void testUserDto() {
        UserDto dto = new UserDto(1L, "Test User", "test@email.com");

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@email.com", dto.getEmail());
    }

    @Test
    void testUserDtoNoArgsConstructor() {
        UserDto dto = new UserDto();
        assertNotNull(dto);
    }

    @Test
    void testUserDtoAllArgsConstructor() {
        UserDto dto = new UserDto(2L, "User 2", "user2@email.com");
        assertEquals(2L, dto.getId());
        assertEquals("User 2", dto.getName());
        assertEquals("user2@email.com", dto.getEmail());
    }
}
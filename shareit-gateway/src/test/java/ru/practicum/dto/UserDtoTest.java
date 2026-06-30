package ru.practicum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserDto_ShouldPassValidation() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail("test@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyName_ShouldFailValidation() {
        UserDto dto = new UserDto();
        dto.setName("");
        dto.setEmail("test@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Имя не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void invalidEmail_ShouldFailValidation() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail("invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Неверный формат email", violations.iterator().next().getMessage());
    }

    @Test
    void nullName_ShouldFailValidation() {
        UserDto dto = new UserDto();
        dto.setName(null);
        dto.setEmail("test@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }

    @Test
    void nullEmail_ShouldFailValidation() {
        UserDto dto = new UserDto();
        dto.setName("Test User");
        dto.setEmail(null);

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
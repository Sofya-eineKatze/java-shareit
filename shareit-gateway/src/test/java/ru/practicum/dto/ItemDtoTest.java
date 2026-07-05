package ru.practicum.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validItemDto_ShouldPassValidation() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyName_ShouldFailValidation() {
        ItemDto dto = new ItemDto();
        dto.setName("");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void emptyDescription_ShouldFailValidation() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("");
        dto.setAvailable(true);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Описание не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void nullAvailable_ShouldFailValidation() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals("Статус доступности должен быть указан", violations.iterator().next().getMessage());
    }
}
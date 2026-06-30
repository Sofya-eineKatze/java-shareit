package ru.practicum.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ErrorHandler;
import ru.practicum.exception.ErrorResponse;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_ShouldReturn404() {
        NotFoundException exception = new NotFoundException("Объект не найден");

        ErrorResponse response = errorHandler.handleNotFound(exception);

        assertNotNull(response);
        assertEquals("Объект не найден", response.getError());
    }

    @Test
    void handleValidation_ShouldReturn400() {
        ValidationException exception = new ValidationException("Ошибка валидации");

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertNotNull(response);
        assertEquals("Ошибка валидации", response.getError());
    }

    @Test
    void handleConflict_ShouldReturn409() {
        ConflictException exception = new ConflictException("Конфликт данных");

        ErrorResponse response = errorHandler.handleConflict(exception);

        assertNotNull(response);
        assertEquals("Конфликт данных", response.getError());
    }

    @Test
    void handleForbidden_ShouldReturn403() {
        ForbiddenException exception = new ForbiddenException("Доступ запрещён");

        ErrorResponse response = errorHandler.handleForbidden(exception);

        assertNotNull(response);
        assertEquals("Доступ запрещён", response.getError());
    }

    @Test
    void handleOther_ShouldReturn500() {
        Exception exception = new RuntimeException("Внутренняя ошибка");

        ErrorResponse response = errorHandler.handleOther(exception);

        assertNotNull(response);
        assertEquals("Внутренняя ошибка сервера", response.getError());
    }
}
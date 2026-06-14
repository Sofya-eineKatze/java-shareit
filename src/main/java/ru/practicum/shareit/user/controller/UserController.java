package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users - получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - получение пользователя", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - создание пользователя: {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - обновление пользователя: {}", id, userDto);
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - удаление пользователя", id);
        userService.deleteUser(id);
    }
}
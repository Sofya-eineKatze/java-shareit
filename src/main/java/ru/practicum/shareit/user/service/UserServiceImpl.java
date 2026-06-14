package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (!userDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Неверный формат email: " + userDto.getEmail());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Email уже существует: " + userDto.getEmail());
        }
        User user = userMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        log.info("Создан пользователь с id {}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getEmail() != null && userRepository.existsByEmailAndIdNot(userDto.getEmail(), id)) {
            throw new ConflictException("Email уже существует: " + userDto.getEmail());
        }

        userMapper.updateEntity(existing, userDto);
        User updated = userRepository.save(existing);
        log.info("Обновлён пользователь с id {}", updated.getId());
        return userMapper.toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
        log.info("Удалён пользователь с id {}", id);
    }
}
package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toEntity(UserDto dto) {
        if (dto == null) return null;
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }
}
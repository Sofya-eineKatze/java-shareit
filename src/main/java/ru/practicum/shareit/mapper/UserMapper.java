package ru.practicum.shareit.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.dto.UserDto;
import ru.practicum.shareit.model.User;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        if (user == null) return null;
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) return null;
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public void updateEntity(User existing, UserDto source) {
        if (source.getName() != null) {
            existing.setName(source.getName());
        }
        if (source.getEmail() != null) {
            existing.setEmail(source.getEmail());
        }
    }
}
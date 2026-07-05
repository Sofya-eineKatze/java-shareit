package ru.practicum.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toDto_ShouldMapAllFields() {
        User user = new User(1L, "Alice", "alice@example.com");

        UserDto dto = userMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Alice");
        assertThat(dto.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void toDto_NullUser_ShouldReturnNull() {
        UserDto dto = userMapper.toDto(null);

        assertThat(dto).isNull();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        UserDto dto = new UserDto(1L, "Alice", "alice@example.com");

        User user = userMapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void toEntity_NullDto_ShouldReturnNull() {
        User user = userMapper.toEntity(null);

        assertThat(user).isNull();
    }
}
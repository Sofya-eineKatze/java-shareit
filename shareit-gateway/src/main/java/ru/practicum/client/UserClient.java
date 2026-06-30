package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    public UserClient(@Value("${shareit-server.url}") String serverUrl) {
        super(serverUrl);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users");
    }

    public ResponseEntity<Object> getUserById(Long id) {
        return get("/users/" + id);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("/users", userDto);
    }

    public ResponseEntity<Object> updateUser(Long id, UserDto userDto) {
        return patch("/users/" + id, userDto);
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        return delete("/users/" + id);
    }
}
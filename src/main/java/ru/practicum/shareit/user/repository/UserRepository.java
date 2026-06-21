package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<String, Long> emailToId = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idCounter.getAndIncrement());
        }
        users.put(user.getId(), user);
        emailToId.put(user.getEmail(), user.getId());
        return user;
    }

    public void deleteById(Long id) {
        User user = users.remove(id);
        if (user != null) {
            emailToId.remove(user.getEmail());
        }
    }

    public boolean existsByEmail(String email) {
        return emailToId.containsKey(email);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        Long existingId = emailToId.get(email);
        return existingId != null && !existingId.equals(id);
    }
}
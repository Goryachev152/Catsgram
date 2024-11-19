package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User createUser(User user) {
        if (user.getEmail().isBlank())
            throw new ConditionsNotMetException("Имейл должен быть указан");
        if (users.values().stream().anyMatch(user1 -> user1.equals(user))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = users.get(user.getId());
        if (user.getEmail() != null) {
            if (!oldUser.equals(user)) {
                if (users.values().stream().anyMatch(user1 -> user1.equals(user))) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                } else {
                    oldUser.setEmail(user.getEmail());
                }
            }
        }
        if (user.getUsername() != null) {
            oldUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            oldUser.setPassword(user.getPassword());
        }
        users.put(user.getId(), oldUser);
        return oldUser;
    }

    public Optional<User> findUserById(Long authorId) {
        if (users.containsKey(authorId)) {
            return Optional.of(users.get(authorId));
        } else {
            return Optional.empty();
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

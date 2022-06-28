package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long userId = 0L;

    @Override
    public User add(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("New user added: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (isUserExists(user.getId())) {
            users.put(user.getId(), user);
            log.info("User updated: id={}", user.getId());
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", user.getId()));
        }
    }

    @Override
    public User remove(User user) {
        if (isUserExists(user.getId())) {
            return users.remove(user.getId());
        } else {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", user.getId()));
        }
    }

    @Override
    public Optional<User> getById(Long id) {
        if (isUserExists(id)) {
            return Optional.of(users.get(id));
        } else {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", id));
        }
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public boolean isUserExists(Long id) {
        return users.containsKey(id);
    }

    private Long generateId() {
        return ++userId;
    }
}
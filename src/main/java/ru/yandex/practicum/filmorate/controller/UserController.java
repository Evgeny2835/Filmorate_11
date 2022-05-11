package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.UserId;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping({"/users"})
    public User create(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            logWarnAndThrowException("Пользователь существует");
        }
        if (user.getEmail().isEmpty() ||
                user.getEmail() == null ||
                !user.getEmail().contains("@")) {
            logWarnAndThrowException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin().isBlank()) {
            logWarnAndThrowException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            logWarnAndThrowException("Дата рождения не может быть в будущем");
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(UserId.getUserId());
        log.info("Добавлен пользователь: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping({"/users"})
    public User update(@RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            logWarnAndThrowException("Пользователь не существует");
        }
        log.info("Обновлен пользователь: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping({"/users"})
    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    private void logWarnAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }
}
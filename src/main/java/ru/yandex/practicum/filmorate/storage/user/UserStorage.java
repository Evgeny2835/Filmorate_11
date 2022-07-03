package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User remove(User user);

    Optional<User> getById(Long id);

    Collection<User> getUsers();

    boolean isUserExists(Long id);
}
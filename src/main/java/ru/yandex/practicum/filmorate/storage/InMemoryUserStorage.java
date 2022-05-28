package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.UserId;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final UserId id = new UserId();

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            String message = String.format("Пользователь существует: %s", user.getName());
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getEmail().isEmpty() ||
                user.getEmail() == null ||
                !user.getEmail().contains("@")) {
            String message = "Электронная почта не может быть пустой и должна содержать символ @";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getLogin().isBlank()) {
            String message = "Логин не может быть пустым и содержать пробелы";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(id.getUserId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            String message = String.format("Пользователь не существует: %s", user.getName());
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public List<User> findAll() {
        if (users.isEmpty()) {
            String message = "Пользователи не существуют";
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long id) {
        if (!users.containsKey(id)) {
            writeLogAndThrowExceptionIfUserIdNotExist(id);
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            writeLogAndThrowExceptionIfUserIdNotExist(userId);
        }
        if (!users.containsKey(friendId)) {
            writeLogAndThrowExceptionIfUserIdNotExist(friendId);
        }
        users.get(userId).getFriendsId().add(friendId);
        users.get(friendId).getFriendsId().add(userId);
        log.info("Пользователь {} добавлен в друзья пользователю {}",
                users.get(friendId).getName(),
                users.get(userId).getName());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!users.containsKey(userId)) {
            writeLogAndThrowExceptionIfUserIdNotExist(userId);
        }
        if (!users.containsKey(friendId)) {
            writeLogAndThrowExceptionIfUserIdNotExist(friendId);
        }
        users.get(userId).getFriendsId().remove(friendId);
        users.get(friendId).getFriendsId().remove(userId);
        log.info("Пользователь {} удален из друзей пользователя {}",
                users.get(friendId).getName(),
                users.get(userId).getName());
    }

    @Override
    public List<User> getFriends(long id) {
        if (!users.containsKey(id)) {
            writeLogAndThrowExceptionIfUserIdNotExist(id);
        }
        List<Long> idFriends = new ArrayList<>(users.get(id).getFriendsId());
        List<User> friends = new ArrayList<>();
        for (Long idFriend : idFriends) {
            friends.add(users.get(idFriend));
        }
        log.info("Текущее количество друзей пользователя {}: {}",
                users.get(id).getName(),
                users.get(id).getFriendsId().size());
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) {
        if (!users.containsKey(id)) {
            writeLogAndThrowExceptionIfUserIdNotExist(id);
        }
        if (!users.containsKey(otherId)) {
            writeLogAndThrowExceptionIfUserIdNotExist(otherId);
        }
        List<User> commonFriends = new ArrayList<>();
        for (Long userId : users.get(id).getFriendsId()) {
            if (users.get(otherId).getFriendsId().contains(userId)) {
                commonFriends.add(users.get(userId));
            }
        }
        log.info("Текущее количество общих друзей у пользователей {} и {}: {}",
                users.get(id).getName(),
                users.get(otherId).getName(),
                commonFriends.size());
        return commonFriends;
    }

    private void writeLogAndThrowExceptionIfUserIdNotExist(Long id) {
        String message = String.format("Не существует пользователь с id=%d", id);
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }
}
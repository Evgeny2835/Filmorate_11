package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User remove(User user) {
        return userStorage.remove(user);
    }

    public User getById(Long id) {
        return userStorage.getById(id).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User not found: id=%d", id)));
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public void addFriend(Long userId, Long friendId) {
        friendsStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        friendsStorage.removeFriend(userId, friendId);
    }

    public Collection<User> getFriends(Long id) {
        return friendsStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        return getFriends(id).stream()
                .filter(x -> getFriends(otherId).contains(x))
                .collect(Collectors.toList());
    }
}
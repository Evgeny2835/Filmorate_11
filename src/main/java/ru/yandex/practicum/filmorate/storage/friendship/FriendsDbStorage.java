package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

@Component
@Slf4j
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private static final String INSERT_INTO_FRIENDSHIPS_USER_ID_FRIEND_ID_STATUS_VALUES =
            "INSERT INTO FRIENDSHIPS (user_id, friend_id, status) VALUES (?, ?, ?)";
    private static final String DELETE_FROM_FRIENDSHIPS_WHERE_USER_ID_AND_FRIEND_ID =
            "DELETE FROM FRIENDSHIPS WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_FROM_FRIENDSHIPS_JOIN_USERS_WHERE_USER_ID =
            "SELECT FRIEND_ID, EMAIL, LOGIN, NAME, BIRTHDAY " +
                    "FROM FRIENDSHIPS AS F " +
                    "JOIN USERS AS U ON F.FRIEND_ID = U.USER_ID " +
                    "WHERE F.USER_ID = ?";

    public FriendsDbStorage(JdbcTemplate jdbcTemplate, UserDbStorage userDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbStorage = userDbStorage;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userDbStorage.isUserExists(userId) && userDbStorage.isUserExists(friendId)) {
            jdbcTemplate.update(
                    INSERT_INTO_FRIENDSHIPS_USER_ID_FRIEND_ID_STATUS_VALUES, userId, friendId, false);
            log.info("User id={} added user id={} as a friend", userId, friendId);
        } else {
            throw new ObjectNotFoundException(String.format(
                    "User id=%d and/or friend id=%d not found", userId, friendId));
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (userDbStorage.isUserExists(userId) && userDbStorage.isUserExists(friendId)) {
            jdbcTemplate.update(
                    DELETE_FROM_FRIENDSHIPS_WHERE_USER_ID_AND_FRIEND_ID, userId, friendId);
            log.info("User id={} deleted user id={} from friends", userId, friendId);
        } else {
            throw new ObjectNotFoundException(String.format(
                    "User id=%d and/or friend id=%d not found", userId, friendId));
        }
    }

    @Override
    public Collection<User> getFriends(Long id) {
        if (userDbStorage.isUserExists(id)) {
            return jdbcTemplate.query(
                    SELECT_FROM_FRIENDSHIPS_JOIN_USERS_WHERE_USER_ID, (rs, rowNum) -> new User(
                            rs.getLong("friend_id"),
                            rs.getString("email"),
                            rs.getString("login"),
                            rs.getString("name"),
                            rs.getDate("birthday").toLocalDate()),
                    id
            );
        } else {
            throw new ObjectNotFoundException(String.format(
                    "User id=%d not found", id));
        }
    }
}
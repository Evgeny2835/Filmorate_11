package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SET_EMAIL_LOGIN_NAME_BIRTHDAY_WHERE_USER_ID =
            "UPDATE USERS SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private static final String DELETE_FROM_USERS_WHERE_USER_ID =
            "DELETE FROM USERS WHERE user_id = ?";
    private static final String SELECT_FROM_USERS_WHERE_USER_ID =
            "SELECT * FROM USERS WHERE user_id = ?";

    private static final String SELECT_FROM_USERS =
            "SELECT * FROM USERS";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
        log.info("New user added: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (isUserExists(user.getId())) {
            jdbcTemplate.update(SET_EMAIL_LOGIN_NAME_BIRTHDAY_WHERE_USER_ID,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("User updated: id={}", user.getId());
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", user.getId()));
        }
    }

    @Override
    public User remove(User user) {
        if (isUserExists(user.getId())) {
            jdbcTemplate.update(DELETE_FROM_USERS_WHERE_USER_ID, user.getId());
            log.info("User deleted: id={}", user.getId());
            return user;
        } else {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", user.getId()));
        }
    }

    @Override
    public Optional<User> getById(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_FROM_USERS_WHERE_USER_ID, id);
        if (userRows.next()) {
            User user = new User(
                    userRows.getLong("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate()
            );
            log.info("User found: id={}", id);
            return Optional.of(user);
        } else {
            log.info("User not found: id={}", id);
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query(SELECT_FROM_USERS, (rs, rowNum) -> new User(
                rs.getLong("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate())
        );
    }

    @Override
    public boolean isUserExists(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_FROM_USERS_WHERE_USER_ID, id);
        return userRows.next();
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    private UserController userController;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);
        user1 = new User();
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1-login");
        user1.setName("user1-name");
        user1.setBirthday(LocalDate.of(1975, Month.DECEMBER, 9));
        user2 = new User();
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user2-login");
        user2.setName("user2-name");
        user2.setBirthday(LocalDate.of(1935, Month.MARCH, 14));
    }

    @Test
    void createNewUser() {
        userController.create(user1);
        assertFalse(userController.findAll().isEmpty());
    }

    @Test
    void createNewUserWithIncorrectEmailResultIsException() {
        user1.setEmail("");
        user2.setEmail("user2yandex.ru");
        assertThrows(ValidationException.class, () -> userController.create(user1));
        assertThrows(ValidationException.class, () -> userController.create(user2));
    }

    @Test
    void createNewUserWithIncorrectLoginResultIsException() {
        user1.setLogin("");
        user2.setLogin("     ");
        assertThrows(ValidationException.class, () -> userController.create(user1));
        assertThrows(ValidationException.class, () -> userController.create(user2));
    }

    @Test
    void createNewUserWithDateOfBirthInFutureResultIsException() {
        user1.setBirthday(LocalDate.of(2975, Month.DECEMBER, 9));
        assertThrows(ValidationException.class, () -> userController.create(user1));
    }

    @Test
    void createNewUserIfNameIsEmpty() {
        user1.setName("");
        userController.create(user1);
        assertEquals("user1-login", userController.getUser(user1.getId()).getName());
    }

    @Test
    void update() {
        userController.create(user1);
        User user1Updated = new User();
        user1Updated.setId(user1.getId());
        user1Updated.setEmail(user1.getEmail());
        user1Updated.setLogin("user1-login-updated");
        user1Updated.setName(user1.getName());
        user1Updated.setBirthday(user1.getBirthday());
        userController.update(user1Updated);
        assertEquals("user1-login-updated", userController.getUser(user1.getId()).getLogin());
    }

    @Test
    void findAll() {
        userController.create(user1);
        assertEquals(1, userController.findAll().size());
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        //userController = new UserController();
    }

   /* @Test
    void createNewUser() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "user1-login",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        userController.create(newUser1);
        assertFalse(userController.getUsers().isEmpty());
    }*/

   /* @Test
    void createNewUserWithIncorrectEmailResultIsException() {
        User newUser1 = new User(0,
                "",
                "user1-login",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        User newUser2 = new User(0,
                "user2yandex.ru",
                "user2-login",
                "user2-name",
                LocalDate.of(1935, Month.MARCH, 14));
        assertThrows(ValidationException.class, () -> userController.create(newUser1));
        assertThrows(ValidationException.class, () -> userController.create(newUser2));
    }*/

   /* @Test
    void createNewUserWithIncorrectLoginResultIsException() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        User newUser2 = new User(0,
                "user2@yandex.ru",
                "     ",
                "user2-name",
                LocalDate.of(1935, Month.MARCH, 14));
        assertThrows(ValidationException.class, () -> userController.create(newUser1));
        assertThrows(ValidationException.class, () -> userController.create(newUser2));
    }*/

   /* @Test
    void createNewUserWithoutLogin() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "user1-login",
                "",
                LocalDate.of(1975, Month.DECEMBER, 9));
        userController.create(newUser1);
        assertEquals("user1-login", userController.getUsers().get(1).getName());
    }*/

   /* @Test
    void createNewUserWithDateOfBirthInFutureResultIsException() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "user1-login",
                "user1-name",
                LocalDate.of(2975, Month.DECEMBER, 9));
        assertThrows(ValidationException.class, () -> userController.create(newUser1));
    }*/

  /*  @Test
    void update() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "user1-login",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        User newUser1Updated = new User(1,
                "user1@yandex.ru",
                "user1-login-updated",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        userController.create(newUser1);
        userController.update(newUser1Updated);
        assertEquals("user1-login-updated", userController.getUsers().get(1).getLogin());
    }*/

  /*  @Test
    void findAll() {
        User newUser1 = new User(0,
                "user1@yandex.ru",
                "user1-login",
                "user1-name",
                LocalDate.of(1975, Month.DECEMBER, 9));
        userController.create(newUser1);
        assertEquals(1, userController.getUsers().size());
    }*/
}
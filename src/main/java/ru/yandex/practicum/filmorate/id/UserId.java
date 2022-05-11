package ru.yandex.practicum.filmorate.id;

public class UserId {
    private static int userId;

    public static int getUserId() {
        return ++userId;
    }
}
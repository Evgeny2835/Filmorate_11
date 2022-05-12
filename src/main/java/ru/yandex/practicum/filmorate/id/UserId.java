package ru.yandex.practicum.filmorate.id;

public class UserId {
    private int userId;

    public int getUserId() {
        return ++userId;
    }
}
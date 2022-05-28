package ru.yandex.practicum.filmorate.id;

public class UserId {
    private long userId;

    public long getUserId() {
        return ++userId;
    }
}
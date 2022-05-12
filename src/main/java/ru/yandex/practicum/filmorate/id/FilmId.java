package ru.yandex.practicum.filmorate.id;

public class FilmId {
    private int filmId;

    public int getFilmId() {
        return ++filmId;
    }
}
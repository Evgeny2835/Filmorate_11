package ru.yandex.practicum.filmorate.id;

public class FilmId {
    private static int filmId;

    public static int getFilmId() {
        return ++filmId;
    }
}

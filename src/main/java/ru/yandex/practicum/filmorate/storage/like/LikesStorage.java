package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface LikesStorage {

    void addLike(Long userId, Long filmId);

    void removeLike(Long filmId, Long userId);

    Collection<Film> getPopular(Long count);
}
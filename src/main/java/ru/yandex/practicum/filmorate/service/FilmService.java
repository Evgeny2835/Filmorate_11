package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikesStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;
    private static final LocalDate RELEASEDATE = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
    }

    public Film add(Film film) {
        if (film.getReleaseDate().isBefore(RELEASEDATE))
            throw new ValidationException("The release date can't be earlier 28-12-1895");
        filmStorage.add(film);
        return film;
    }

    public Film update(Film film) {
        filmStorage.update(film);
        Film filmSetGenres = filmStorage.getById(film.getId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Film not found: id=%d", film.getId())));
        if (film.getGenres() == null) {
            filmSetGenres.setGenres(null);
        } else if (film.getGenres().isEmpty()) {
            filmSetGenres.setGenres(new LinkedHashSet<>());
        }
        return filmSetGenres;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id).
                orElseThrow(() ->  new ObjectNotFoundException(String.format("Film not found: id=%d", id)));
    }

    public void addLike(Long filmId, Long userId) {
        likesStorage.addLike(filmId, userId);
        log.info("User id={} set like film id={}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        likesStorage.removeLike(filmId, userId);
        log.info("User id={} deleted like to film id={}", userId, filmId);
    }

    public Collection<Film> getPopular(Long count) {
        return likesStorage.getPopular(count);
    }
}
package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long filmId = 0L;

    @Override
    public Film add(Film film) {
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("New film added: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isFilmExists(film.getId())) {
            films.put(film.getId(), film);
            log.info("Film updated: id={}", film.getId());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", film.getId()));
        }
    }

    @Override
    public Film remove(Film film) {
        if (isFilmExists(film.getId())) {
            log.info("Film deleted: id={}", film.getId());
            return films.remove(film.getId());
        } else {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", film.getId()));
        }
    }

    @Override
    public Optional<Film> getById(Long id) {
        if (isFilmExists(id)) {
            return Optional.of(films.get(id));
        } else {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", id));
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public boolean isFilmExists(Long id) {
        return films.containsKey(id);
    }

    private Long generateId() {
        return ++filmId;
    }
}
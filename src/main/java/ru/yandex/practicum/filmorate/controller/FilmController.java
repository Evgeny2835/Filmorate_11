package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.FilmId;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/films"})
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final FilmId filmId = new FilmId();

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            logWarnAndThrowException("Фильм существует");
        }
        if (film.getName().isEmpty() || film.getName() == null) {
            logWarnAndThrowException("Название не может быть пустым");
        }
        if (film.getDescription().length() == 0 || film.getDescription().length() > 200) {
            logWarnAndThrowException("Описание - обязательный атрибут, максимальная длина - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            logWarnAndThrowException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration().isNegative() || film.getDuration().isZero()) {
            logWarnAndThrowException("Продолжительность фильма должна быть положительной");
        }
        film.setId(filmId.getFilmId());
        log.info("Добавлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            logWarnAndThrowException("Фильм не существует");
        }
        log.info("Обновлен фильм: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> findAll() {
        log.info("Доступно фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Map<Integer, Film> getFilms() {
        return new HashMap<>(films);
    }

    private void logWarnAndThrowException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }
}
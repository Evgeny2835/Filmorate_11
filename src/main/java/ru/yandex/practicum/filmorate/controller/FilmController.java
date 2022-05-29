package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Component
@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final String MESSAGE = "Параметры должны принимать положительные значения";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        if (id > 0) {
            return filmService.getFilm(id);
        }
        String message = "Не найден";
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable Long userId) {
        if (id <= 0 || userId <= 0) {
            writeLogAndThrowValidationException();
        }
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable Long userId) {
        if (id > 0 && userId > 0) {
            filmService.removeLike(id, userId);
            return;
        }
        String message = "Не найден";
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        if (count <= 0) {
            writeLogAndThrowValidationException();
        }
        return filmService.getPopularFilms(count);
    }

    private void writeLogAndThrowValidationException() {
        log.warn(MESSAGE);
        throw new ValidationException(MESSAGE);
    }
}
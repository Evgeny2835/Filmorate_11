package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.id.FilmId;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmId filmId = new FilmId();
    private static final LocalDate FIRST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            writeLogAndThrowValidationException("Фильм существует");
        }
        if (film.getName().isEmpty() || film.getName() == null) {
            writeLogAndThrowValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() == 0 || film.getDescription().length() > 200) {
            writeLogAndThrowValidationException("Описание - обязательный атрибут, максимальная длина - 200 символов");
        }
        if (film.getReleaseDate().isBefore(FIRST_RELEASE_DATE)) {
            writeLogAndThrowValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            writeLogAndThrowValidationException("Продолжительность фильма должна быть положительной");
        }
        film.setId(filmId.getFilmId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            String message = String.format("Фильм не существует: %s", film.getName());
            log.warn(message);
            throw new ObjectNotFoundException(message);
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм: {}", film);
        return film;
    }

    public List<Film> findAll() {
        log.info("Доступно фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film getFilm(Long id) {
        if (!films.containsKey(id)) {
            writeLogAndThrowExceptionIfIdFilmNotExist(id);
        }
        log.info("Выдан фильм: {}", films.get(id).getName());
        return films.get(id);
    }

    public void addLike(Long id, Long userId) {
        if (!films.containsKey(id)) {
            writeLogAndThrowExceptionIfIdFilmNotExist(id);
        }
        for (User user : userStorage.findAll()) {
            if (user.getId() == userId) {
                films.get(id).getLikes().add(userId);
                log.info("Пользователь {} добавил лайк фильму {}",
                        user.getName(),
                        films.get(id).getName());
                return;
            }
        }
        String message = String.format("Не существует пользователь с id=%d", userId);
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    public void removeLike(Long id, Long userId) {
        if (!films.containsKey(id)) {
            writeLogAndThrowExceptionIfIdFilmNotExist(id);
        }
        for (User user : userStorage.findAll()) {
            if (user.getId() == userId) {
                films.get(id).getLikes().remove(userId);
                log.info("Пользователь {} удалил лайк фильму {}",
                        user.getName(),
                        films.get(id).getName());
                return;
            }
        }
        String message = String.format("Не существует пользователь с id=%d", userId);
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }

    public List<Film> getPopularFilms(Long count) {
        return films.values().stream().sorted((p0, p1) -> {
            int comp = Integer.compare(p0.getLikes().size(), p1.getLikes().size());
            return comp * -1;
        }).limit(count).collect(Collectors.toList());
    }

    private void writeLogAndThrowValidationException(String message) {
        log.warn(message);
        throw new ValidationException(message);
    }

    private void writeLogAndThrowExceptionIfIdFilmNotExist(Long id) {
        String message = String.format("Не существует фильм с id=%d", id);
        log.warn(message);
        throw new ObjectNotFoundException(message);
    }
}
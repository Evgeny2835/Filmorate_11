package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    private FilmController filmController;
    private Film film1;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        FilmStorage filmStorage = new InMemoryFilmStorage(userStorage);
        FilmService filmService = new FilmService(filmStorage);
        filmController = new FilmController(filmService);
        film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Film1-description");
        film1.setReleaseDate(LocalDate.of(1980, Month.JANUARY, 17));
        film1.setDuration(100);
    }

    @Test
    void createNewFilm() {
        filmController.create(film1);
        assertFalse(filmController.findAll().isEmpty());
    }

    @Test
    void createNewFilmWithoutNameIsException() {
        film1.setName("");
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    void createNewFilmWithDescriptionLength200() {
        film1.setDescription("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        filmController.create(film1);
        assertFalse(filmController.findAll().isEmpty());
    }

    @Test
    void createNewFilmWithDescriptionLength201IsException() {
        film1.setDescription("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB");
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    void createNewFilmWithDescriptionLength0IsException() {
        film1.setDescription("");
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    void createNewFilmWithOldReleaseDateIsException() {
        film1.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    void createNewFilmWithReleaseDate_1895_12_28() {
        film1.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28));
        filmController.create(film1);
        assertFalse(filmController.findAll().isEmpty());
    }

    @Test
    void createNewFilmWithNegativeDurationIsException() {
        film1.setDuration(-100);
        assertThrows(ValidationException.class, () -> filmController.create(film1));
    }

    @Test
    void update() {
        filmController.create(film1);
        Film film1Updated = new Film();
        film1Updated.setId(film1.getId());
        film1Updated.setName("Film1Update");
        film1Updated.setDescription(film1.getDescription());
        film1Updated.setReleaseDate(film1.getReleaseDate());
        film1Updated.setDuration(film1.getDuration());
        filmController.update(film1Updated);
        assertEquals("Film1Update", filmController.getFilm(film1.getId()).getName());
    }

    @Test
    void findAll() {
        filmController.create(film1);
        assertEquals(1, filmController.findAll().size());
    }
}
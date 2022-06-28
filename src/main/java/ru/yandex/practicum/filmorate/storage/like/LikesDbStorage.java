package ru.yandex.practicum.filmorate.storage.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Component
@Slf4j
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public LikesDbStorage(JdbcTemplate jdbcTemplate,
                          GenreStorage genreStorage,
                          MpaStorage mpaStorage,
                          @Qualifier("filmDbStorage") FilmStorage filmStorage,
                          @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
        if (!filmStorage.isFilmExists(filmId)) {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", filmId));
        }
        String sql = "INSERT INTO LIKES (user_id, film_id) VALUES (? , ?)";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("User id={} added like to film id={}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
        if (!filmStorage.isFilmExists(filmId)) {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", filmId));
        }
        String sql = "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sql, userId, filmId);
        log.info("User id={} deleted like to film id={}", userId, filmId);
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        String sql = "SELECT FILMS.FILM_ID, NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_ID , " +
                "COUNT(L.USER_ID) as RATING FROM FILMS LEFT JOIN LIKES L ON FILMS.FILM_ID = L.FILM_ID " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY RATING DESC LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                genreStorage.getFilmGenres(rs.getLong("film_id")),
                mpaStorage.getMpa(rs.getInt("mpa_id")),
                rs.getLong("rating")
        ), count);
        return films;
    }
}
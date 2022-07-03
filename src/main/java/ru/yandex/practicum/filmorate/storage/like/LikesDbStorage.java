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
    private static final String INSERT_INTO_LIKES_USER_ID_FILM_ID_VALUES =
            "INSERT INTO LIKES (user_id, film_id) VALUES (? , ?)";
    private static final String DELETE_FROM_LIKES_WHERE_USER_ID_AND_FILM_ID =
            "DELETE FROM LIKES WHERE user_id = ? AND film_id = ?";
    private static final String SELECT_FROM_FILMS_LEFT_JOIN_GROUP_BY_ORDER_BY_DESC_LIMIT =
            "SELECT FILMS.FILM_ID, NAME, DESCRIPTION, RELEASEDATE, DURATION, MPA_ID , " +
                    "COUNT(L.USER_ID) as RATING FROM FILMS LEFT JOIN LIKES L ON FILMS.FILM_ID = L.FILM_ID " +
                    "GROUP BY FILMS.FILM_ID " +
                    "ORDER BY RATING DESC LIMIT ?";

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
        jdbcTemplate.update(INSERT_INTO_LIKES_USER_ID_FILM_ID_VALUES, userId, filmId);
        log.info("User id={} added like to film id={}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        if (!userStorage.isUserExists(userId)) {
            throw new ObjectNotFoundException(String.format("User not found: id=%d", userId));
        }
        if (!filmStorage.isFilmExists(filmId)) {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", filmId));
        }
        jdbcTemplate.update(DELETE_FROM_LIKES_WHERE_USER_ID_AND_FILM_ID, userId, filmId);
        log.info("User id={} deleted like to film id={}", userId, filmId);
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return jdbcTemplate.query(
                SELECT_FROM_FILMS_LEFT_JOIN_GROUP_BY_ORDER_BY_DESC_LIMIT, (rs, rowNum) -> new Film(
                        rs.getLong("film_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDate("releaseDate").toLocalDate(),
                        rs.getInt("duration"),
                        genreStorage.getFilmGenres(rs.getLong("film_id")),
                        mpaStorage.getMpa(rs.getInt("mpa_id")),
                        rs.getLong("rating")
                ), count);
    }
}
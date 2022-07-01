package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private static final String SET_MPA_ID_WHERE_FILM_ID =
            "UPDATE FILMS SET mpa_id = ? WHERE film_id = ?";
    private static final String SET_NAME_DESCRIPTION_RELEASEDATE_DURATION_MPA_ID_WHERE_FILM_ID =
            "UPDATE FILMS SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_FROM_FILMS_WHERE_FILM_ID =
            "DELETE FROM FILMS WHERE film_id = ?";
    private static final String SELECT_FROM_FILMS_WHERE_FILM_ID =
            "SELECT * FROM FILMS WHERE film_id = ?";
    private static final String SELECT_FROM_FILMS =
            "SELECT * FROM FILMS";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, MpaStorage mpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        genreStorage.addGenres(film);
        jdbcTemplate.update(SET_MPA_ID_WHERE_FILM_ID,
                film.getMpa().getId(),
                film.getId()
        );
        log.info("New film added: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (isFilmExists(film.getId())) {
            jdbcTemplate.update(SET_NAME_DESCRIPTION_RELEASEDATE_DURATION_MPA_ID_WHERE_FILM_ID,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            genreStorage.removeGenres(film);
            genreStorage.addGenres(film);
            log.info("Film updated: id={}", film.getId());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", film.getId()));
        }
    }

    @Override
    public Film remove(Film film) {
        if (isFilmExists(film.getId())) {
            jdbcTemplate.update(DELETE_FROM_FILMS_WHERE_FILM_ID, film.getId());
            log.info("Film updated: id={}", film.getId());
            return film;
        } else {
            throw new ObjectNotFoundException(String.format("Film not found: id=%d", film.getId()));
        }
    }

    @Override
    public Optional<Film> getById(Long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(SELECT_FROM_FILMS_WHERE_FILM_ID, id);
        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("name"),
                    filmRows.getString("description"),
                    filmRows.getDate("releaseDate").toLocalDate(),
                    filmRows.getInt("duration")
            );
            int mpaId = filmRows.getInt("mpa_id");
            film.setMpa(mpaStorage.getMpa(mpaId));
            Set<Genre> genres = genreStorage.getFilmGenres(id);
            if (genres.size() != 0) {
                film.setGenres(genreStorage.getFilmGenres(id));
            }
            log.info("Found film: id={}", id);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query(SELECT_FROM_FILMS, (rs, rowNum) -> new Film(
                rs.getLong("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("releaseDate").toLocalDate(),
                rs.getInt("duration"),
                genreStorage.getFilmGenres(rs.getLong("film_id")),
                mpaStorage.getMpa(rs.getInt("mpa_id"))
        ));
    }

    @Override
    public boolean isFilmExists(Long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_FROM_FILMS_WHERE_FILM_ID, id);
        return userRows.next();
    }
}
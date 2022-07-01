package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
@Slf4j
public class GenreService {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_FROM_GENRES = "SELECT * FROM GENRES";
    private static final String SELECT_NAME_FROM_GENRES_WHERE_GENRE_ID =
            "SELECT NAME FROM GENRES WHERE GENRE_ID = ?";

    public GenreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Genre> get() {
        return jdbcTemplate.query(SELECT_FROM_GENRES, ((rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"))
        ));
    }

    public Genre get(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_NAME_FROM_GENRES_WHERE_GENRE_ID, id);
        if (userRows.next()) {
            Genre genre = new Genre(
                    id,
                    userRows.getString("name")
            );
            log.info("Genre found = {} ", genre);
            return genre;
        } else throw new ObjectNotFoundException(String.format("Genre not found: id=%d", id));
    }
}
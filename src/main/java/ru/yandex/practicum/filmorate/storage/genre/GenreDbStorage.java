package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO FILM_GENRES (film_id, genre_id) VALUES (?, ?)",
                        film.getId(), genre.getId());
            }
        }
    }

    @Override
    public void removeGenres(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES" +
                " WHERE film_id = ?", film.getId());
    }

    @Override
    public HashSet<Genre> getFilmGenres(Long filmId) {
        String sql = "SELECT GENRES.GENRE_ID, NAME FROM FILM_GENRES\n" +
                "    JOIN GENRES ON FILM_GENRES.GENRE_ID = GENRES.GENRE_ID\n" +
                "    WHERE FILM_ID = ?\n" +
                "    ORDER BY GENRES.GENRE_ID";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("name")),
                filmId
        ));
    }
}
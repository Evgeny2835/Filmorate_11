package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@Slf4j
public class MpaService {
    private final JdbcTemplate jdbcTemplate;

    public MpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Mpa> getMpa() {
        String sql = "SELECT * FROM MPA_RATING";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("name"))
        );
    }

    public Mpa get(int id) {
        String sql = "SELECT NAME FROM MPA_RATING WHERE MPA_ID = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        if (userRows.next()) {
            Mpa mpa = new Mpa(
                    id,
                    userRows.getString("name")
            );
            log.info("Mpa found: {}", mpa);
            return mpa;
        } else throw new ObjectNotFoundException(String.format("Mpa not found: id=%d", id));
    }
}
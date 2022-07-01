package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID =
            "SELECT NAME FROM MPA_RATING WHERE MPA_ID = ?";

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpa(int mpaId) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SELECT_NAME_FROM_MPA_RATING_WHERE_MPA_ID, mpaId);
        if (userRows.next()) {
            return new Mpa(mpaId, userRows.getString("name"));
        }
        return null;
    }
}
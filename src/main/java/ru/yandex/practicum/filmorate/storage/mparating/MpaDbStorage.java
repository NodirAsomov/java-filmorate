package ru.yandex.practicum.filmorate.storage.mparating;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbc;

    @Override
    public List<MpaRating> findAll() {
        return jdbc.query(
                "SELECT * FROM mpa",
                (rs, rowNum) -> new MpaRating(
                        rs.getInt("id"),
                        rs.getString("name")
                )
        );
    }

    @Override
    public Optional<MpaRating> findById(int id) {
        return jdbc.query(
                "SELECT * FROM mpa WHERE id = ?",
                (rs, rowNum) -> new MpaRating(
                        rs.getInt("id"),
                        rs.getString("name")
                ),
                id
        ).stream().findFirst();
    }
}

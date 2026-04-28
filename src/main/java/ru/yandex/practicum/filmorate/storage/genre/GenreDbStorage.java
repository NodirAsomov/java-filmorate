package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbc;

    @Override
    public List<Genre> findAll() {
        return jdbc.query("""
                        SELECT id, name FROM genres ORDER BY id
                        """,
                (rs, rowNum) -> new Genre(
                        rs.getLong("id"),
                        rs.getString("name")
                ));
    }

    @Override
    public Optional<Genre> findById(Long id) {
        return jdbc.query("""
                        SELECT id, name FROM genres WHERE id=?
                        """,
                (rs, rowNum) -> new Genre(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                id
        ).stream().findFirst();
    }

    @Override
    public List<Genre> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        String sql = """
                SELECT id, name
                FROM genres
                WHERE id IN (%s)
                ORDER BY id
                """.formatted(inSql);

        return jdbc.query(
                sql,
                (rs, rowNum) -> new Genre(
                        rs.getLong("id"),
                        rs.getString("name")
                ),
                ids.toArray()
        );
    }
}

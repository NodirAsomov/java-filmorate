package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;


@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        String sql = """
                    INSERT INTO films (name, description, release_date, duration, mpa_id)
                    VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Long.class);
        film.setId(id);

        setGenres(film.getId(), film.getGenres().stream().map(Genre::getId).collect(java.util.stream.Collectors.toSet()));

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update("""
                            UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=?
                            WHERE id=?
                        """,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        setGenres(film.getId(), film.getGenres().stream().map(Genre::getId).collect(java.util.stream.Collectors.toSet()));

        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        List<Film> films = jdbcTemplate.query(
                "SELECT * FROM films WHERE id=?",
                this::mapRow,
                id
        );
        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        return jdbcTemplate.query("SELECT * FROM films", this::mapRow);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", id);
        jdbcTemplate.update("DELETE FROM film_likes WHERE film_id=?", id);
        jdbcTemplate.update("DELETE FROM films WHERE id=?", id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(
                "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)",
                filmId, userId
        );
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update(
                "DELETE FROM film_likes WHERE film_id=? AND user_id=?",
                filmId, userId
        );
    }

    @Override
    public List<Film> findPopular(int count) {
        return jdbcTemplate.query("""
                    SELECT f.*, COUNT(fl.user_id) AS likes_count
                    FROM films f
                    LEFT JOIN film_likes fl ON f.id = fl.film_id
                    GROUP BY f.id
                    ORDER BY likes_count DESC
                    LIMIT ?
                """, this::mapRow, count);
    }

    @Override
    public void setGenres(long filmId, Set<Integer> genreIds) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", filmId);

        for (Integer id : genreIds) {
            jdbcTemplate.update(
                    "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                    filmId, id
            );
        }
    }

    @Override
    public Set<Genre> getGenres(long filmId) {
        return new java.util.HashSet<>(jdbcTemplate.query(
                """
                        SELECT g.id FROM genres g
                        JOIN film_genres fg ON g.id = fg.genre_id
                        WHERE fg.film_id=?
                        """,
                (rs, rowNum) -> Genre.fromId(rs.getInt("id")),
                filmId
        ));
    }

    private Film mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(MpaRating.fromId(rs.getInt("mpa_id")));
        film.setGenres(getGenres(film.getId()));
        return film;
    }
}

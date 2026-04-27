package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Film create(Film film) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());

            return ps;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        film.setId(id);

        updateGenres(id, film.getGenres());

        return findById(id).orElseThrow();
    }

    @Override
    public Film update(Film film) {

        jdbc.update("""
                        UPDATE films
                        SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                        WHERE id = ?
                        """,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        updateGenres(film.getId(), film.getGenres());

        return findById(film.getId()).orElseThrow();
    }

    @Override
    public Optional<Film> findById(long id) {
        return jdbc.query("""
                        SELECT f.*, m.name AS mpa_name
                        FROM films f
                        JOIN mpa m ON f.mpa_id = m.id
                        WHERE f.id = ?
                        """,
                this::mapRow,
                id
        ).stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query("""
                        SELECT f.*, m.name AS mpa_name
                        FROM films f
                        JOIN mpa m ON f.mpa_id = m.id
                        """,
                this::mapRow
        );
    }

    @Override
    public void delete(long id) {
        jdbc.update("DELETE FROM films WHERE id = ?", id);
    }


    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update("""
                INSERT INTO film_likes (film_id, user_id)
                VALUES (?, ?)
                """, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update("""
                DELETE FROM film_likes
                WHERE film_id = ? AND user_id = ?
                """, filmId, userId);
    }

    @Override
    public List<Film> findPopular(int count) {
        return jdbc.query("""
                        SELECT f.*, m.name AS mpa_name
                        FROM films f
                        JOIN mpa m ON f.mpa_id = m.id
                        LEFT JOIN film_likes fl ON f.id = fl.film_id
                        GROUP BY f.id, m.name
                        ORDER BY COUNT(fl.user_id) DESC
                        LIMIT ?
                        """,
                this::mapRow,
                count
        );
    }

    @Override
    public void setGenres(long filmId, List<Integer> genreIds) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                genreIds,
                genreIds.size(),
                (ps, genreId) -> {
                    ps.setLong(1, filmId);
                    ps.setInt(2, genreId);
                }
        );
    }

    @Override
    public List<Genre> getGenres(long filmId) {
        return jdbc.query("""
                        SELECT g.id, g.name
                        FROM genres g
                        JOIN film_genres fg ON g.id = fg.genre_id
                        WHERE fg.film_id = ?
                        ORDER BY g.id
                        """,
                (rs, rowNum) -> new Genre(
                        rs.getInt("id"),
                        rs.getString("name")
                ),
                filmId
        );
    }

    private Film mapRow(ResultSet rs, int rowNum) throws SQLException {

        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        film.setMpa(new MpaRating(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name")
        ));

        film.setGenres(getGenres(film.getId()));

        return film;
    }

    private void updateGenres(long filmId, List<Genre> genres) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);

        if (genres == null || genres.isEmpty()) {
            return;
        }

        jdbc.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                genres,
                genres.size(),
                (ps, genre) -> {
                    ps.setLong(1, filmId);
                    ps.setInt(2, genre.getId());
                }
        );
    }
}
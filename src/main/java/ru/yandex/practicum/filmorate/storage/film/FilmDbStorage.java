package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbc;

    @Override
    public Film create(Film film) {
        jdbc.update("""
                            INSERT INTO films (name, description, release_date, duration, mpa_id)
                            VALUES (?, ?, ?, ?, ?)
                        """,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        Long id = jdbc.queryForObject("SELECT MAX(id) FROM films", Long.class);
        film.setId(id);

        setGenres(id, film.getGenres().stream().map(Genre::getId).collect(java.util.stream.Collectors.toSet()));

        return film;
    }

    @Override
    public Film update(Film film) {
        jdbc.update("""
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

        setGenres(film.getId(),
                film.getGenres().stream().map(Genre::getId).collect(java.util.stream.Collectors.toSet())
        );

        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        return jdbc.query("SELECT * FROM films WHERE id=?",
                this::mapRow, id).stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query("SELECT * FROM films", this::mapRow);
    }

    @Override
    public void delete(long id) {
        jdbc.update("DELETE FROM film_likes WHERE film_id=?", id);
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", id);
        jdbc.update("DELETE FROM films WHERE id=?", id);
    }


    @Override
    public void addLike(long filmId, long userId) {
        jdbc.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        jdbc.update("DELETE FROM film_likes WHERE film_id=? AND user_id=?", filmId, userId);
    }


    @Override
    public List<Film> findPopular(int count) {
        return jdbc.query("""
                    SELECT f.*
                    FROM films f
                    LEFT JOIN film_likes fl ON f.id = fl.film_id
                    GROUP BY f.id
                    ORDER BY COUNT(fl.user_id) DESC
                    LIMIT ?
                """, this::mapRow, count);
    }


    @Override
    public void setGenres(long filmId, Set<Integer> genreIds) {
        jdbc.update("DELETE FROM film_genres WHERE film_id=?", filmId);

        for (Integer id : genreIds) {
            jdbc.update("""
                        INSERT INTO film_genres (film_id, genre_id)
                        VALUES (?, ?)
                    """, filmId, id);
        }
    }

    @Override
    public Set<Genre> getGenres(long filmId) {
        return new HashSet<>(jdbc.query("""
                    SELECT g.id
                    FROM genres g
                    JOIN film_genres fg ON g.id = fg.genre_id
                    WHERE fg.film_id=?
                """, (rs, rowNum) -> Genre.fromId(rs.getInt("id")), filmId));
    }


    private Film mapRow(ResultSet rs, int rowNum) throws java.sql.SQLException {
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
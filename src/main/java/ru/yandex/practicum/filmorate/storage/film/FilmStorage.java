package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import ru.yandex.practicum.filmorate.model.Genre;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Optional<Film> findById(long id);

    List<Film> findAll();

    void delete(long id);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    List<Film> findPopular(int count);

    void setGenres(long filmId, Set<Integer> genreIds);

    Set<Genre> getGenres(long filmId);
}



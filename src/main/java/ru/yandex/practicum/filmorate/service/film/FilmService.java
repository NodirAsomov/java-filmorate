package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;


@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        validate(film);
        normalize(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validate(film);
        normalize(film);
        getFilmOrThrow(film.getId());
        return filmStorage.update(film);
    }

    public Film getFilm(long id) {
        return getFilmOrThrow(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public void addLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть > 0");
        }
        return filmStorage.findPopular(count);
    }

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание > 200 символов");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата слишком ранняя");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность должна быть > 0");
        }

        if (film.getMpa() == null) {
            throw new ValidationException("MPA обязателен");
        }
    }


    private void normalize(Film film) {

        if (film.getMpa() == null) {
            throw new ValidationException("MPA не может быть null");
        }

        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        } else {

            film.setGenres(
                    film.getGenres().stream()
                            .distinct()
                            .toList()
            );
        }
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    private void getUserOrThrow(long id) {
        userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}

package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mparating.MpaService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService;


    public Film createFilm(Film film) {
        validateFilm(film);
        enrichFilmForSave(film);
        return filmStorage.create(film);
    }


    public Film updateFilm(Film film) {
        getFilmOrThrow(film.getId());

        validateFilm(film);
        enrichFilmForSave(film);

        return filmStorage.update(film);
    }


    public Film getFilm(long id) {
        Film film = getFilmOrThrow(id);
        enrichFilm(film);
        return film;
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll()
                .stream()
                .peek(this::enrichFilm)
                .toList();
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть > 0");
        }

        return filmStorage.findPopular(count)
                .stream()
                .peek(this::enrichFilm)
                .toList();
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


    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Неверная дата релиза");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Длительность должна быть > 0");
        }

        if (film.getMpa() == null) {
            throw new ValidationException("MPA обязателен");
        }
    }


    private void enrichFilmForSave(Film film) {
        film.setMpa(mpaService.getById(film.getMpa().getId()));

        if (film.getGenres() == null) {
            film.setGenres(List.of());
            return;
        }

        film.setGenres(
                film.getGenres().stream()
                        .map(g -> genreService.getById(g.getId()))
                        .distinct()
                        .toList()
        );
    }

    private void enrichFilm(Film film) {
        film.setGenres(filmStorage.getGenres(film.getId()));
    }


    private Film getFilmOrThrow(long id) {
        return filmStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id " + id + " не найден"));
    }

    private void getUserOrThrow(long id) {
        userStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
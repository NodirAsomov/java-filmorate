package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mparating.MpaService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreStorage genreStorage;

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

        Map<Long, List<Genre>> map =
                filmStorage.getGenresByFilmIds(List.of(id));

        film.setGenres(map.getOrDefault(id, List.of()));

        return film;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.findAll();
        enrichFilms(films);
        return films;
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("count должен быть > 0");
        }

        List<Film> films = filmStorage.findPopular(count);
        enrichFilms(films);
        return films;
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

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            throw new ValidationException("У фильма должен быть хотя бы один жанр");
        }

        List<Long> ids = film.getGenres().stream()
                .map(Genre::getId)
                .distinct()
                .toList();

        List<Genre> genresFromDb = genreStorage.findByIds(ids);

        if (genresFromDb.size() != ids.size()) {
            throw new NotFoundException("Один из жанров не существует");
        }

        film.setGenres(genresFromDb);
    }

    private void enrichFilms(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Long, List<Genre>> genresMap =
                filmStorage.getGenresByFilmIds(filmIds);

        for (Film film : films) {
            film.setGenres(
                    genresMap.getOrDefault(film.getId(), List.of())
            );
        }
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
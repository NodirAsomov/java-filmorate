package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        getFilmOrThrow(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(long id) {
        return getFilmOrThrow(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        if (!film.getLikes().add(user.getId())) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        if (!film.getLikes().remove(user.getId())) {
            throw new ValidationException("Лайк от пользователя не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным");
        }

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }


    private void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }


        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }


        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895");
        }


        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }


    private Film getFilmOrThrow(long filmId) {
        return filmStorage.getFilm(filmId)
                .orElseThrow(() ->
                        new NotFoundException("Фильм с id " + filmId + " не найден")
                );
    }


    private User getUserOrThrow(long userId) {
        return userStorage.getUser(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " + userId + " не найден")
                );
    }

}







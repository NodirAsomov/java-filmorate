package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll().stream().toList();
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        userStorage.getById(userId);
        Film film = filmStorage.getById(filmId);
        film.getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getById(filmId);
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(Integer count) {
        int limit = count == null ? 10 : count;

        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(
                        (Film film) -> film.getLikes().size()
                ).reversed())
                .limit(limit)
                .toList();
    }
}

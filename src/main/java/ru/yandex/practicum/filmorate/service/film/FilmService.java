package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikes().add(userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (!film.getLikes().remove(userId)) {
            throw new ValidationException("Лайк от пользователя не найден");
        }
    }

    public List<Film> getPopularFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Количество фильмов должно быть положительным");
        }
        List<Film> allFilms = filmStorage.getAllFilms();
        allFilms.sort((f1, f2) -> f2.getLikes().size() - f1.getLikes().size());
        return allFilms.subList(0, Math.min(count, allFilms.size()));
    }
}


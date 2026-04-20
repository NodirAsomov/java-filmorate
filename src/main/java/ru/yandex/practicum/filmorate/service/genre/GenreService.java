package ru.yandex.practicum.filmorate.service.genre;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {

    public List<Genre> getAll() {
        return List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );
    }

    public Genre getById(int id) {
        return getAll().stream()
                .filter(g -> g.getId() == id)
                .findFirst()
                .orElseThrow();
    }
}

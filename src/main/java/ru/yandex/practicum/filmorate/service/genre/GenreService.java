package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAll() {
        return genreStorage.findAll();
    }

    public Genre getById(long id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Genre not found: " + id));
    }


    public List<Genre> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Genre> genres = genreStorage.findByIds(ids);


        if (genres.size() != ids.size()) {
            throw new NotFoundException("Один из жанров не существует");
        }

        return genres;
    }
}

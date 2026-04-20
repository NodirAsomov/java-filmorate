package ru.yandex.practicum.filmorate.service.mparating;


import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Service
public class MpaService {

    public List<MpaRating> getAll() {
        return List.of(
                new MpaRating(1, "G"),
                new MpaRating(2, "PG"),
                new MpaRating(3, "PG-13"),
                new MpaRating(4, "R"),
                new MpaRating(5, "NC-17")
        );
    }

    public MpaRating getById(int id) {
        return getAll().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElseThrow();
    }
}

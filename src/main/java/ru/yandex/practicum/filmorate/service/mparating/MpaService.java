package ru.yandex.practicum.filmorate.service.mparating;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mparating.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<MpaRating> getAll() {
        return mpaStorage.findAll();
    }

    public MpaRating getById(int id) {
        return mpaStorage.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("MPA с id " + id + " не найден"));
    }
}


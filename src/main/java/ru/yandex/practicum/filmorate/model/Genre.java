
package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;

    public static Genre fromId(int id) {
        switch (id) {
            case 1: return new Genre(1, "Комедия");
            case 2: return new Genre(2, "Драма");
            case 3: return new Genre(3, "Мультфильм");
            case 4: return new Genre(4, "Триллер");
            case 5: return new Genre(5, "Документальный");
            case 6: return new Genre(6, "Боевик");
            default: throw new IllegalArgumentException("Invalid genre id: " + id);
        }
    }
}
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
        return switch (id) {
            case 1 -> new Genre(1, "COMEDY");
            case 2 -> new Genre(2, "DRAMA");
            case 3 -> new Genre(3, "CARTOON");
            case 4 -> new Genre(4, "THRILLER");
            case 5 -> new Genre(5, "DOCUMENTARY");
            case 6 -> new Genre(6, "ACTION");
            default -> throw new IllegalArgumentException("Invalid genre id: " + id);
        };
    }
}
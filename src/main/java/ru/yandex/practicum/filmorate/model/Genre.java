package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    private Long id;
    private String name;

    public static Genre fromId(long id) {
        return switch ((int) id) {
            case 1 -> new Genre(1L, "COMEDY");
            case 2 -> new Genre(2L, "DRAMA");
            case 3 -> new Genre(3L, "CARTOON");
            case 4 -> new Genre(4L, "THRILLER");
            case 5 -> new Genre(5L, "DOCUMENTARY");
            case 6 -> new Genre(6L, "ACTION");
            default -> throw new IllegalArgumentException("Invalid genre id: " + id);
        };
    }
}
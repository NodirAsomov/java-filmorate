

package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaRating {
    private int id;
    private String name;

    public static MpaRating fromId(int id) {
        switch (id) {
            case 1: return new MpaRating(1, "G");
            case 2: return new MpaRating(2, "PG");
            case 3: return new MpaRating(3, "PG-13");
            case 4: return new MpaRating(4, "R");
            case 5: return new MpaRating(5, "NC-17");
            default: throw new IllegalArgumentException("Unknown MPA id: " + id);
        }
    }
}
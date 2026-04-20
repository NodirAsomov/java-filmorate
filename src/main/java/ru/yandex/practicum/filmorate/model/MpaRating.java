package ru.yandex.practicum.filmorate.model;

public enum MpaRating {
    G, PG, PG_13, R, NC_17;

    // если нужен метод fromId, добавь его
    public static MpaRating fromId(int id) {
        switch (id) {
            case 1: return G;
            case 2: return PG;
            case 3: return PG_13;
            case 4: return R;
            case 5: return NC_17;
            default: throw new IllegalArgumentException("Unknown MPA id: " + id);
        }
    }

    public int getId() {
        switch (this) {
            case G: return 1;
            case PG: return 2;
            case PG_13: return 3;
            case R: return 4;
            case NC_17: return 5;
            default: throw new IllegalArgumentException();
        }
    }
}

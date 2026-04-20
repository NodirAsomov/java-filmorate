package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY(1),
    DRAMA(2),
    CARTOON(3),
    THRILLER(4),
    DOCUMENTARY(5),
    ACTION(6);

    private final int id;

    Genre(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Метод для поиска по id
    public static Genre fromId(int id) {
        for (Genre genre : Genre.values()) {
            if (genre.getId() == id) return genre;
        }
        throw new IllegalArgumentException("Invalid genre id: " + id);
    }
}


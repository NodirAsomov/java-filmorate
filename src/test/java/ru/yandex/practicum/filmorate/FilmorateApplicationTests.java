package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {


        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();


        FilmService filmService = new FilmService(filmStorage);
        UserService userService = new UserService(userStorage);


        filmController = new FilmController(filmService, filmStorage);
        userController = new UserController(userService, userStorage);
    }

    @Test
    void shouldAddValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Научно-фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Film saved = filmController.createFilm(film); // заменили addFilm -> createFilm
        assertNotNull(saved.getId());
        assertEquals("Интерстеллар", saved.getName());
    }

    @Test
    void shouldThrowWhenFilmNameEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> filmController.createFilm(film)); // заменили addFilm -> createFilm
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldGetAllFilms() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        filmController.createFilm(film); // заменили addFilm -> createFilm
        Collection<Film> films = filmController.getAllFilms();
        assertFalse(films.isEmpty());
    }

    @Test
    void shouldAddValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("Пользователь");

        User saved = userController.createUser(user);
        assertNotNull(saved.getId());
        assertEquals("Пользователь", saved.getName());
    }

    @Test
    void shouldSetNameAsLoginIfNameEmpty() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        user.setName("");

        User saved = userController.createUser(user);
        assertEquals("user1", saved.getName());
    }

    @Test
    void shouldThrowWhenEmailInvalid() {
        User user = new User();
        user.setEmail("invalid email.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> userController.createUser(user));
        assertEquals("Email должен содержать символ @", ex.getMessage());
    }

    @Test
    void shouldGetAllUsers() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        userController.createUser(user);
        Collection<User> users = userController.getAllUsers();
        assertFalse(users.isEmpty());
    }
}


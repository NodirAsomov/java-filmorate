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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();

        FilmService filmService = new FilmService(filmStorage, userStorage);
        UserService userService = new UserService(userStorage);


        filmController = new FilmController(filmService);
        userController = new UserController(userService);
    }


    @Test
    void shouldAddValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Научно-фантастический фильм");
        film.setReleaseDate(LocalDate.of(2014, 11, 7));
        film.setDuration(169);

        Film saved = filmController.createFilm(film);
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
                () -> filmController.createFilm(film));
        assertEquals("Название фильма не может быть пустым", ex.getMessage());
    }

    @Test
    void shouldGetAllFilms() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);

        filmController.createFilm(film);
        Collection<Film> films = filmController.getAllFilms();
        assertFalse(films.isEmpty());
    }

    @Test
    void shouldAddAndRemoveLike() {
        Film film = new Film();
        film.setName("Film1");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        Film savedFilm = filmController.createFilm(film);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userController.createUser(user);


        filmController.addLike(savedFilm.getId(), savedUser.getId());
        assertTrue(savedFilm.getLikes().contains(savedUser.getId()));

        filmController.removeLike(savedFilm.getId(), savedUser.getId());
        assertFalse(savedFilm.getLikes().contains(savedUser.getId()));
    }

    @Test
    void shouldReturnPopularFilms() {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(100);
        Film saved1 = filmController.createFilm(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(110);
        Film saved2 = filmController.createFilm(film2);

        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userController.createUser(user);

        filmController.addLike(saved1.getId(), savedUser.getId());

        List<Film> popular = filmController.getPopular(10);
        assertEquals(saved1.getId(), popular.get(0).getId());
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

    @Test
    void shouldAddAndRemoveFriend() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User saved1 = userController.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User saved2 = userController.createUser(user2);


        userController.addFriend(saved1.getId(), saved2.getId());
        assertTrue(saved1.getFriends().contains(saved2.getId()));
        assertTrue(saved2.getFriends().contains(saved1.getId()));


        userController.removeFriend(saved1.getId(), saved2.getId());
        assertFalse(saved1.getFriends().contains(saved2.getId()));
        assertFalse(saved2.getFriends().contains(saved1.getId()));
    }

    @Test
    void shouldGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User saved1 = userController.createUser(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User saved2 = userController.createUser(user2);

        User user3 = new User();
        user3.setEmail("user3@example.com");
        user3.setLogin("user3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        User saved3 = userController.createUser(user3);


        userController.addFriend(saved1.getId(), saved3.getId());
        userController.addFriend(saved2.getId(), saved3.getId());

        List<User> common = userController.getCommonFriends(saved1.getId(), saved2.getId());
        assertEquals(1, common.size());
        assertEquals(saved3.getId(), common.get(0).getId());
    }
}


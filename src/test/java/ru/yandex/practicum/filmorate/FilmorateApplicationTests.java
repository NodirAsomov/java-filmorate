package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	private final FilmController filmController = new FilmController();
	private final UserController userController = new UserController();

	@Test
	void contextLoads() {
	}

	@Test
	void shouldAddValidFilm() {
		Film film = new Film();
		film.setName("Интерстеллар");
		film.setDescription("Научно-фантастический фильм");
		film.setReleaseDate(LocalDate.of(2014, 11, 7));
		film.setDuration(169);

		Film saved = filmController.addFilm(film);
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
				() -> filmController.addFilm(film));
		assertEquals("Название фильма не может быть пустым", ex.getMessage());
	}

	@Test
	void shouldThrowWhenFilmDescriptionTooLong() {
		Film film = new Film();
		film.setName("Film");
		film.setDescription("A".repeat(201));
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(100);

		ValidationException ex = assertThrows(ValidationException.class,
				() -> filmController.addFilm(film));
		assertEquals("Описание не более 200 символов", ex.getMessage());
	}

	@Test
	void shouldThrowWhenFilmReleaseDateTooEarly() {
		Film film = new Film();
		film.setName("Film");
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(1800, 1, 1));
		film.setDuration(100);

		ValidationException ex = assertThrows(ValidationException.class,
				() -> filmController.addFilm(film));
		assertEquals("Дата релиза не может быть раньше 28.12.1895", ex.getMessage());
	}

	@Test
	void shouldThrowWhenFilmDurationNotPositive() {
		Film film = new Film();
		film.setName("Film");
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(0);

		ValidationException ex = assertThrows(ValidationException.class,
				() -> filmController.addFilm(film));
		assertEquals("Продолжительность должна быть положительной", ex.getMessage());
	}

	@Test
	void shouldGetAllFilms() {
		Film film = new Film();
		film.setName("Film");
		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(100);

		filmController.addFilm(film);
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
	void shouldThrowWhenLoginInvalid() {
		User user = new User();
		user.setEmail("user@example.com");
		user.setLogin("user 1");
		user.setBirthday(LocalDate.of(1990, 1, 1));

		ValidationException ex = assertThrows(ValidationException.class,
				() -> userController.createUser(user));
		assertEquals("Логин не может быть пустым или содержать пробелы", ex.getMessage());
	}

	@Test
	void shouldThrowWhenBirthdayInFuture() {
		User user = new User();
		user.setEmail("user@example.com");
		user.setLogin("user1");
		user.setBirthday(LocalDate.now().plusDays(1));

		ValidationException ex = assertThrows(ValidationException.class,
				() -> userController.createUser(user));
		assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
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


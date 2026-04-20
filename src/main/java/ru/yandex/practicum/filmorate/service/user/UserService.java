package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserOrThrow(user.getId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.update(user);
    }

    public User getUser(long id) {
        return getUserOrThrow(id);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public void deleteUser(long id) {
        getUserOrThrow(id);
        userStorage.delete(id);
    }


    public void addFriend(long userId, long friendId) {
        validateDifferent(userId, friendId);
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.addFriendRequest(userId, friendId);
    }

    public void confirmFriend(long userId, long friendId) {
        validateDifferent(userId, friendId);
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.confirmFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        validateDifferent(userId, friendId);
        getUserOrThrow(userId);
        getUserOrThrow(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        getUserOrThrow(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateDifferent(userId, otherId);
        getUserOrThrow(userId);
        getUserOrThrow(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }


    private User getUserOrThrow(long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private void validateDifferent(long a, long b) {
        if (a == b) {
            throw new ValidationException("Нельзя выполнять операцию с самим собой");
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

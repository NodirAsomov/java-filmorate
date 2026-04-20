
package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
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

    public void deleteUser(long id) {
        getUserOrThrow(id);
        userStorage.delete(id);
    }

    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    public void addFriend(long userId, long friendId) {
        validateDifferentUsers(userId, friendId);
        User user = getUserOrThrow(userId);

        if (user.getFriends().containsKey(friendId)) {
            throw new ValidationException("Заявка уже отправлена или пользователь уже друг");
        }


        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    public void confirmFriend(long userId, long friendId) {
        validateDifferentUsers(userId, friendId);
        User user = getUserOrThrow(userId);

        if (!user.getFriends().containsKey(friendId) ||
                user.getFriends().get(friendId) != FriendshipStatus.UNCONFIRMED) {
            throw new ValidationException("Нет заявки на подтверждение от этого пользователя");
        }


        user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
    }

    public void removeFriend(long userId, long friendId) {
        validateDifferentUsers(userId, friendId);
        User user = getUserOrThrow(userId);

        if (!user.getFriends().containsKey(friendId)) {
            throw new ValidationException("Пользователь не найден в друзьях");
        }

        user.getFriends().remove(friendId);
    }

    public List<User> getFriends(long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> getUserOrThrow(e.getKey()))
                .toList();
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        validateDifferentUsers(userId, otherId);
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);

        return user.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(e -> e.getKey())
                .filter(other.getFriends().keySet()::contains)
                .map(this::getUserOrThrow)
                .toList();
    }

    private User getUserOrThrow(long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с id " + userId + " не найден")
                );
    }

    private void validateDifferentUsers(long firstId, long secondId) {
        if (firstId == secondId) {
            throw new ValidationException("Операция с одним и тем же пользователем недопустима");
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}

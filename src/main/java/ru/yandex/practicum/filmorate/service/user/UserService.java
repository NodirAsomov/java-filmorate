package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> findAll() {
        return userStorage.findAll().stream().toList();
    }

    public User getById(Integer id) {
        return userStorage.getById(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Integer userId) {
        return userStorage.getById(userId).getFriends().stream()
                .map(userStorage::getById)
                .toList();
    }

    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        Set<Integer> common = new HashSet<>(userStorage.getById(userId).getFriends());
        common.retainAll(userStorage.getById(otherId).getFriends());

        return common.stream()
                .map(userStorage::getById)
                .toList();
    }
}

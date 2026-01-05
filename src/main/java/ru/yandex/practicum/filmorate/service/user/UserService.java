package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);

        if (!user.getFriends().add(friendId)) {
            throw new ValidationException("Пользователь уже в друзьях");
        }
        friend.getFriends().add(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);


        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }


    public List<User> getFriends(long userId) {
        User user = userStorage.getUser(userId);
        List<User> friends = new ArrayList<>();
        for (Long id : user.getFriends()) {
            friends.add(userStorage.getUser(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.getUser(userId);
        User other = userStorage.getUser(otherId);

        Set<Long> common = new HashSet<>(user.getFriends());
        common.retainAll(other.getFriends());

        List<User> result = new ArrayList<>();
        for (Long id : common) {
            result.add(userStorage.getUser(id));
        }
        return result;
    }
}



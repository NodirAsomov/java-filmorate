package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;


    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }


    @Override
    public User create(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE users
                SET email = ?, login = ?, name = ?, birthday = ?
                WHERE id = ?
                """;

        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        return jdbc.query(sql, this::mapRow, id)
                .stream()
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users", this::mapRow);
    }

    @Override
    public void delete(long id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }


    @Override
    public void addFriend(long userId, long friendId) {
        String sql = """
                INSERT INTO friendships(user_id, friend_id, status)
                VALUES (?, ?, 'CONFIRMED')
                """;

        jdbc.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = """
                DELETE FROM friendships
                WHERE user_id = ? AND friend_id = ?
                """;

        jdbc.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendships f ON u.id = f.friend_id
                WHERE f.user_id = ?
                """;

        return jdbc.query(sql, this::mapRow, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        String sql = """
                SELECT u.*
                FROM users u
                JOIN friendships f1 ON u.id = f1.friend_id
                JOIN friendships f2 ON u.id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?
                """;

        return jdbc.query(sql, this::mapRow, userId, otherId);
    }
}
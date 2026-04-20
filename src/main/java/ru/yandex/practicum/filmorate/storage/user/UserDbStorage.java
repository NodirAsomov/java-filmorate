package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    @Override
    public User create(User user) {
        String sql = """
                    INSERT INTO users (email, login, name, birthday)
                    VALUES (?, ?, ?, ?)
                """;

        jdbc.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        Long id = jdbc.queryForObject("SELECT MAX(id) FROM users", Long.class);
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        jdbc.update("""
                            UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?
                        """,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        List<User> users = jdbc.query(
                "SELECT * FROM users WHERE id=?",
                this::mapRow,
                id
        );
        return users.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users", this::mapRow);
    }

    @Override
    public void delete(long id) {
        jdbc.update("DELETE FROM friendships WHERE user_id=? OR friend_id=?", id, id);
        jdbc.update("DELETE FROM users WHERE id=?", id);
    }


    @Override
    public void addFriend(long userId, long friendId) {
        jdbc.update("""
                    INSERT INTO friendships (user_id, friend_id, status)
                    VALUES (?, ?, 'UNCONFIRMED')
                """, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbc.update("""
                    DELETE FROM friendships WHERE user_id=? AND friend_id=?
                """, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return jdbc.query("""
                    SELECT u.*
                    FROM users u
                    JOIN friendships f ON u.id = f.friend_id
                    WHERE f.user_id = ? AND f.status = 'CONFIRMED'
                """, this::mapRow, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return jdbc.query("""
                    SELECT u.*
                    FROM users u
                    JOIN friendships f1 ON u.id = f1.friend_id
                    JOIN friendships f2 ON u.id = f2.friend_id
                    WHERE f1.user_id = ? AND f2.user_id = ?
                """, this::mapRow, userId, otherId);
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
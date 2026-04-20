package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        jdbcTemplate.update("""
                            INSERT INTO users (email, login, name, birthday)
                            VALUES (?, ?, ?, ?)
                        """,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Long.class);
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("""
                            UPDATE users SET email=?, login=?, name=?, birthday=?
                            WHERE id=?
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
        return jdbcTemplate.query(
                "SELECT * FROM users WHERE id=?",
                this::mapRow,
                id
        ).stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", this::mapRow);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM film_likes WHERE user_id=?", id);
        jdbcTemplate.update("DELETE FROM friendships WHERE user_id=? OR friend_id=?", id, id);
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
    }

    @Override
    public void addFriendRequest(long userId, long friendId) {
        jdbcTemplate.update("""
                    INSERT INTO friendships (user_id, friend_id, status)
                    VALUES (?, ?, 'UNCONFIRMED')
                """, userId, friendId);
    }

    @Override
    public void confirmFriend(long userId, long friendId) {
        jdbcTemplate.update("""
                    UPDATE friendships
                    SET status='CONFIRMED'
                    WHERE user_id=? AND friend_id=?
                """, friendId, userId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        jdbcTemplate.update("""
                    DELETE FROM friendships
                    WHERE user_id=? AND friend_id=?
                """, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        return jdbcTemplate.query("""
                    SELECT u.* FROM users u
                    JOIN friendships f ON u.id = f.friend_id
                    WHERE f.user_id=? AND f.status='CONFIRMED'
                """, this::mapRow, userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherId) {
        return jdbcTemplate.query("""
                    SELECT u.* FROM users u
                    JOIN friendships f1 ON u.id = f1.friend_id
                    JOIN friendships f2 ON u.id = f2.friend_id
                    WHERE f1.user_id=? AND f2.user_id=?
                    AND f1.status='CONFIRMED'
                    AND f2.status='CONFIRMED'
                """, this::mapRow, userId, otherId);
    }

    private User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
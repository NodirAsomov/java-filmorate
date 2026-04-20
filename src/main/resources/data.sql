
DELETE FROM friendships;
DELETE FROM film_likes;
DELETE FROM film_genres;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN id RESTART WITH 1;
ALTER TABLE genres ALTER COLUMN id RESTART WITH 1;
ALTER TABLE mpa ALTER COLUMN id RESTART WITH 1;


INSERT INTO users (email, login, name, birthday) VALUES
('alice@mail.com', 'alice', 'Alice', '1990-05-15'),
('bob@mail.com', 'bob', 'Bob', '1988-10-20'),
('carol@mail.com', 'carol', 'Carol', '1995-12-01');


INSERT INTO friendships (user_id, friend_id) VALUES
(1, 2),
(2, 3);


INSERT INTO genres (id, name) VALUES
(1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');


INSERT INTO mpa (id, name) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');


INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Интерстеллар', 'Фантастический фильм про космос', '2014-11-07', 169, 3),
('Титаник', 'Романтическая драма', '1997-12-19', 195, 3),
('Корпорация монстров', 'Мультфильм', '2001-11-02', 92, 1);


INSERT INTO film_genres (film_id, genre_id) VALUES
(1, 2),
(1, 4),
(2, 2),
(3, 1),
(3, 3);


INSERT INTO film_likes (film_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(3, 3);
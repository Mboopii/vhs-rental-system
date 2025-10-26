-- Dodavanje 6 korisnika
INSERT INTO users (name, email) VALUES ('Ana Anić', 'ana.anic@example.com');
INSERT INTO users (name, email) VALUES ('Marko Markić', 'marko.markic@example.com');
INSERT INTO users (name, email) VALUES ('Jelena Jelić', 'jelena.jelic@example.com');
INSERT INTO users (name, email) VALUES ('Ivan Ivić', 'ivan.ivic@example.com');
INSERT INTO users (name, email) VALUES ('Petra Perić', 'petra.peric@example.com');
INSERT INTO users (name, email) VALUES ('Luka Lukić', 'luka.lukic@example.com');

-- Dodavanje 20 filmova
INSERT INTO vhs (title, genre, release_year) VALUES ('The Shawshank Redemption', 'Drama', 1994);
INSERT INTO vhs (title, genre, release_year) VALUES ('The Godfather', 'Crime', 1972);
INSERT INTO vhs (title, genre, release_year) VALUES ('The Dark Knight', 'Action', 2008);
INSERT INTO vhs (title, genre, release_year) VALUES ('Pulp Fiction', 'Crime', 1994);
INSERT INTO vhs (title, genre, release_year) VALUES ('Forrest Gump', 'Drama', 1994);
INSERT INTO vhs (title, genre, release_year) VALUES ('Inception', 'Sci-Fi', 2010);
INSERT INTO vhs (title, genre, release_year) VALUES ('The Matrix', 'Sci-Fi', 1999);
INSERT INTO vhs (title, genre, release_year) VALUES ('Se7en', 'Thriller', 1995);
INSERT INTO vhs (title, genre, release_year) VALUES ('Gladiator', 'Action', 2000);
INSERT INTO vhs (title, genre, release_year) VALUES ('Alien', 'Horror', 1979);
INSERT INTO vhs (title, genre, release_year) VALUES ('Back to the Future', 'Sci-Fi', 1985);
INSERT INTO vhs (title, genre, release_year) VALUES ('Terminator 2: Judgment Day', 'Action', 1991);
INSERT INTO vhs (title, genre, release_year) VALUES ('The Silence of the Lambs', 'Thriller', 1991);
INSERT INTO vhs (title, genre, release_year) VALUES ('Star Wars: Episode IV - A New Hope', 'Sci-Fi', 1977);
INSERT INTO vhs (title, genre, release_year) VALUES ('Jurassic Park', 'Sci-Fi', 1993);
INSERT INTO vhs (title, genre, release_year) VALUES ('The Lion King', 'Animation', 1994);
INSERT INTO vhs (title, genre, release_year) VALUES ('Braveheart', 'Drama', 1995);
INSERT INTO vhs (title, genre, release_year) VALUES ('Goodfellas', 'Crime', 1990);
INSERT INTO vhs (title, genre, release_year) VALUES ('Fight Club', 'Drama', 1999);
INSERT INTO vhs (title, genre, release_year) VALUES ('Casablanca', 'Romance', 1942);

INSERT INTO rental (user_id, vhs_id, rental_date, due_date, return_date, late_fee)
VALUES (1, 1, '2025-10-10', '2025-10-17', NULL, NULL);

INSERT INTO rental (user_id, vhs_id, rental_date, due_date, return_date, late_fee)
VALUES (2, 2, '2025-10-08', '2025-10-18', '2025-10-20', 5.0);
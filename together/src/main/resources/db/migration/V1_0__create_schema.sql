BEGIN;

CREATE EXTENSION pg_trgm;
CREATE EXTENSION btree_gin;

CREATE TYPE roles AS ENUM ('ROLE_USER', 'ROLE_ADMIN');

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(63) NOT NULL UNIQUE,
  email VARCHAR(127) NOT NULL UNIQUE,
  password VARCHAR(127) NOT NULL,
  bio TEXT,
  photo VARCHAR(255)
);

CREATE TABLE cafes (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  poster VARCHAR(255),
  address VARCHAR(255),
  working_hours VARCHAR(100),
  price NUMERIC(10, 2),
  alcohol_permission BOOLEAN,
  smoking_permission BOOLEAN,
  owner_id INTEGER REFERENCES users(id) ON DELETE RESTRICT
);
CREATE TABLE games (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  genre VARCHAR(50),
  age_constraint INTEGER,
  description VARCHAR(255) NOT NULL,
  cafe_id INTEGER REFERENCES cafes(id)
);

CREATE TABLE drinks (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20),
  ingredients VARCHAR(200),
  cost INTEGER,
  is_alcoholic BOOLEAN,
  cafe_id INTEGER REFERENCES cafes(id)
);

CREATE TABLE foods (
  id SERIAL PRIMARY KEY,
  name VARCHAR(20),
  ingredients VARCHAR(200),
  cost INTEGER,
  is_hot BOOLEAN,
  is_spicy BOOLEAN,
  cafe_id INTEGER REFERENCES cafes(id)
);

CREATE TABLE hookahs (
  id SERIAL PRIMARY KEY,
  tobacco VARCHAR(30),
  strength INTEGER,
  cost INTEGER,
  taste VARCHAR(30),
  cafe_id INTEGER REFERENCES cafes(id)
);

CREATE TABLE lobbies (
  id SERIAL PRIMARY KEY,
  cafe_id INTEGER NOT NULL REFERENCES cafes(id),
  date TIMESTAMP NOT NULL,
  max_participants INTEGER NOT NULL,
  description VARCHAR(200),
  current_participants INTEGER
);

CREATE TABLE lobby_participants (
  lobby_id INTEGER NOT NULL REFERENCES lobbies(id),
  user_id INTEGER NOT NULL REFERENCES users(id),
  PRIMARY KEY (lobby_id, user_id)
);

CREATE TABLE reports (
  id SERIAL PRIMARY KEY,
  sender_id INT REFERENCES users(id) ON DELETE SET NULL,

  cafe_id INT REFERENCES cafes(id) ON DELETE CASCADE,
  user_id INT REFERENCES users(id) ON DELETE CASCADE ,

  resolved BOOLEAN NOT NULL DEFAULT FALSE,
  resolved_at TIMESTAMP,
  resolver_id INT REFERENCES users(id) ON DELETE SET NULL,

  issue VARCHAR(127) NOT NULL,
  text TEXT,
  date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_report_subject_nn CHECK (cafe_id IS NOT NULL OR user_id IS NOT NULL)
);

CREATE TABLE password_changes (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token VARCHAR(255) NOT NULL,
  date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
  id SERIAL PRIMARY KEY,
  user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role roles NOT NULL,
  UNIQUE(user_id, role)
);

---- Триггеры

-- Удалять токен пароля после 7 дней
CREATE OR REPLACE FUNCTION cleanup_password_tokens()
  RETURNS TRIGGER AS $$
BEGIN
  DELETE FROM password_changes WHERE date < CURRENT_TIMESTAMP - INTERVAL '7 days';
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cleanup_tokens
  AFTER INSERT OR UPDATE ON password_changes
  FOR EACH ROW
EXECUTE FUNCTION cleanup_password_tokens();

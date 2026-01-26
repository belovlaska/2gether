-- Нормализация структуры базы данных
-- Создание нормализованных таблиц и миграция данных

-- 1. Создание таблиц для сущностей
CREATE TABLE ingredients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tastes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Создание промежуточных таблиц для связей many-to-many
-- Связь между едой и ингредиентами
CREATE TABLE food_ingredients (
    food_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    PRIMARY KEY (food_id, ingredient_id),
    FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);

-- Связь между напитками и ингредиентами
CREATE TABLE drink_ingredients (
    drink_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    PRIMARY KEY (drink_id, ingredient_id),
    FOREIGN KEY (drink_id) REFERENCES drinks(id) ON DELETE CASCADE,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE
);

-- Связь между играми и жанрами
CREATE TABLE game_genres (
    game_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (game_id, genre_id),
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Связь между кальянами и вкусами
CREATE TABLE hookah_tastes (
    hookah_id BIGINT NOT NULL,
    taste_id BIGINT NOT NULL,
    PRIMARY KEY (hookah_id, taste_id),
    FOREIGN KEY (hookah_id) REFERENCES hookahs(id) ON DELETE CASCADE,
    FOREIGN KEY (taste_id) REFERENCES tastes(id) ON DELETE CASCADE
);

-- 3. Создание индексов для оптимизации запросов
CREATE INDEX idx_food_ingredients_food ON food_ingredients(food_id);
CREATE INDEX idx_food_ingredients_ingr ON food_ingredients(ingredient_id);
CREATE INDEX idx_drink_ingredients_drink ON drink_ingredients(drink_id);
CREATE INDEX idx_drink_ingredients_ingr ON drink_ingredients(ingredient_id);
CREATE INDEX idx_game_genres_game ON game_genres(game_id);
CREATE INDEX idx_game_genres_genre ON game_genres(genre_id);
CREATE INDEX idx_hookah_tastes_hookah ON hookah_tastes(hookah_id);
CREATE INDEX idx_hookah_tastes_taste ON hookah_tastes(taste_id);

-- 4. Удаление старых столбцов с разделёнными запятыми данными
-- Эти изменения будут выполнены в следующей миграции (V1_5), после переноса данных
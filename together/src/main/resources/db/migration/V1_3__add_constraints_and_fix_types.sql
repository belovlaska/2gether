BEGIN;

-- Добавляем проверки для таблицы пользователей
ALTER TABLE users 
ADD CONSTRAINT chk_username_length CHECK (LENGTH(TRIM(username)) >= 3),
ADD CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

-- Расширяем длины полей для напитков и еды
ALTER TABLE drinks ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE foods ALTER COLUMN name TYPE VARCHAR(100);

-- Расширяем длины полей для кальянов
ALTER TABLE hookahs ALTER COLUMN tobacco TYPE VARCHAR(100);
ALTER TABLE hookahs ALTER COLUMN taste TYPE VARCHAR(100);

-- Добавляем проверки на положительные значения
ALTER TABLE cafes 
ADD CONSTRAINT chk_price_positive CHECK (price IS NULL OR price >= 0);

ALTER TABLE drinks 
ADD CONSTRAINT chk_cost_positive CHECK (cost IS NULL OR cost >= 0);

ALTER TABLE foods 
ADD CONSTRAINT chk_food_cost_positive CHECK (cost IS NULL OR cost >= 0);

ALTER TABLE hookahs 
ADD CONSTRAINT chk_hookah_cost_positive CHECK (cost IS NULL OR cost >= 0);

ALTER TABLE games 
ADD CONSTRAINT chk_age_constraint_non_negative CHECK (age_constraint IS NULL OR age_constraint >= 0);

ALTER TABLE lobbies 
ADD CONSTRAINT chk_max_participants_positive CHECK (max_participants > 0),
ADD CONSTRAINT chk_current_participants_non_negative CHECK (current_participants IS NULL OR current_participants >= 0);

-- Проверка, что максимальное количество участников больше или равно текущему
ALTER TABLE lobbies ADD CONSTRAINT chk_participants_count 
CHECK (current_participants IS NULL OR current_participants <= max_participants);

-- Добавляем уникальность для имени игры в рамках одного кафе
ALTER TABLE games ADD CONSTRAINT uk_games_name_per_cafe UNIQUE (name, cafe_id);

-- Добавляем проверку для силы кальяна
ALTER TABLE hookahs ADD CONSTRAINT chk_strength_range 
CHECK (strength IS NULL OR strength BETWEEN 1 AND 10);

-- Обновляем поля, чтобы избежать NULL в важных столбцах и устанавливаем NOT NULL
-- Сначала обновляем NULL-значения, если они есть
UPDATE drinks SET name = 'Unnamed Drink' WHERE name IS NULL;
UPDATE foods SET name = 'Unnamed Food' WHERE name IS NULL;
UPDATE hookahs SET tobacco = 'Unknown Tobacco' WHERE tobacco IS NULL;
UPDATE hookahs SET taste = 'Unknown Taste' WHERE taste IS NULL;
UPDATE hookahs SET strength = 1 WHERE strength IS NULL;

-- Теперь можем установить NOT NULL
ALTER TABLE drinks ALTER COLUMN name SET NOT NULL;
ALTER TABLE foods ALTER COLUMN name SET NOT NULL;
ALTER TABLE hookahs ALTER COLUMN tobacco SET NOT NULL;
ALTER TABLE hookahs ALTER COLUMN taste SET NOT NULL;
ALTER TABLE hookahs ALTER COLUMN strength SET NOT NULL;

-- Проверка уникальности токенов
ALTER TABLE refresh_tokens ADD CONSTRAINT uk_refresh_tokens_token UNIQUE (token);
ALTER TABLE verification_tokens ADD CONSTRAINT uk_verification_tokens_token UNIQUE (token);

COMMIT;
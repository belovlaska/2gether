-- Удаление старых столбцов с разделёнными запятыми данными
-- После переноса данных в нормализованные таблицы

-- Удаление столбца ingredients из таблицы foods
ALTER TABLE foods DROP COLUMN ingredients;

-- Удаление столбца ingredients из таблицы drinks  
ALTER TABLE drinks DROP COLUMN ingredients;

-- Удаление столбца genre из таблицы games
ALTER TABLE games DROP COLUMN genre;

-- Удаление столбца taste из таблицы hookahs
ALTER TABLE hookahs DROP COLUMN taste;
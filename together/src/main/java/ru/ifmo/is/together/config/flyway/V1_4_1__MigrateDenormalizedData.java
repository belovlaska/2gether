package ru.ifmo.is.together.config.flyway;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Миграция данных из денормализованных столбцов в нормализованные таблицы
 */
public class V1_4_1__MigrateDenormalizedData extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(
            new SingleConnectionDataSource(context.getConnection(), true)
        );

        // Миграция ингредиентов еды
        migrateFoodIngredients(jdbcTemplate);

        // Миграция ингредиентов напитков
        migrateDrinkIngredients(jdbcTemplate);

        // Миграция жанров игр
        migrateGameGenres(jdbcTemplate);

        // Миграция вкусов кальяна
        migrateHookahTastes(jdbcTemplate);
    }

    private void migrateFoodIngredients(JdbcTemplate jdbcTemplate) {
        try {
            // Получаем все записи из таблицы foods с ингредиентами
            String selectSql = "SELECT id, ingredients FROM foods WHERE ingredients IS NOT NULL AND ingredients != ''";
            List<Map<String, Object>> foods = jdbcTemplate.queryForList(selectSql);

            Map<String, Long> ingredientCache = getIngredientCache(jdbcTemplate);

            for (Map<String, Object> food : foods) {
                Integer foodId = (Integer) food.get("id");
                String ingredientsStr = (String) food.get("ingredients");

                if (ingredientsStr != null && !ingredientsStr.trim().isEmpty()) {
                    String[] ingredientsArray = parseCommaSeparatedValues(ingredientsStr);
                    
                    for (String ingredientName : ingredientsArray) {
                        ingredientName = ingredientName.trim();
                        if (!ingredientName.isEmpty()) {
                            Long ingredientId = ingredientCache.computeIfAbsent(
                                ingredientName, 
                                name -> insertAndGetId(jdbcTemplate, "ingredients", name)
                            );
                            
                            // Вставляем связь в промежуточную таблицу
                            insertRelation(jdbcTemplate, "food_ingredients", foodId, ingredientId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error migrating food ingredients", e);
        }
    }

    private void migrateDrinkIngredients(JdbcTemplate jdbcTemplate) {
        try {
            // Получаем все записи из таблицы drinks с ингредиентами
            String selectSql = "SELECT id, ingredients FROM drinks WHERE ingredients IS NOT NULL AND ingredients != ''";
            List<Map<String, Object>> drinks = jdbcTemplate.queryForList(selectSql);

            Map<String, Long> ingredientCache = getIngredientCache(jdbcTemplate);

            for (Map<String, Object> drink : drinks) {
                Integer drinkId = (Integer) drink.get("id");
                String ingredientsStr = (String) drink.get("ingredients");

                if (ingredientsStr != null && !ingredientsStr.trim().isEmpty()) {
                    String[] ingredientsArray = parseCommaSeparatedValues(ingredientsStr);
                    
                    for (String ingredientName : ingredientsArray) {
                        ingredientName = ingredientName.trim();
                        if (!ingredientName.isEmpty()) {
                            Long ingredientId = ingredientCache.computeIfAbsent(
                                ingredientName, 
                                name -> insertAndGetId(jdbcTemplate, "ingredients", name)
                            );
                            
                            // Вставляем связь в промежуточную таблицу
                            insertRelation(jdbcTemplate, "drink_ingredients", drinkId, ingredientId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error migrating drink ingredients", e);
        }
    }

    private void migrateGameGenres(JdbcTemplate jdbcTemplate) {
        try {
            // Получаем все записи из таблицы games с жанрами
            String selectSql = "SELECT id, genre FROM games WHERE genre IS NOT NULL AND genre != ''";
            List<Map<String, Object>> games = jdbcTemplate.queryForList(selectSql);

            Map<String, Long> genreCache = getGenreCache(jdbcTemplate);

            for (Map<String, Object> game : games) {
                Integer gameId = (Integer) game.get("id");
                String genreStr = (String) game.get("genre");

                if (genreStr != null && !genreStr.trim().isEmpty()) {
                    String[] genresArray = parseCommaSeparatedValues(genreStr);
                    
                    for (String genreName : genresArray) {
                        genreName = genreName.trim();
                        if (!genreName.isEmpty()) {
                            Long genreId = genreCache.computeIfAbsent(
                                genreName, 
                                name -> insertAndGetId(jdbcTemplate, "genres", name)
                            );
                            
                            // Вставляем связь в промежуточную таблицу
                            insertRelation(jdbcTemplate, "game_genres", gameId, genreId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error migrating game genres", e);
        }
    }

    private void migrateHookahTastes(JdbcTemplate jdbcTemplate) {
        try {
            // Получаем все записи из таблицы hookahs с вкусами
            String selectSql = "SELECT id, taste FROM hookahs WHERE taste IS NOT NULL AND taste != ''";
            List<Map<String, Object>> hookahs = jdbcTemplate.queryForList(selectSql);

            Map<String, Long> tasteCache = getTasteCache(jdbcTemplate);

            for (Map<String, Object> hookah : hookahs) {
                Integer hookahId = (Integer) hookah.get("id");
                String tasteStr = (String) hookah.get("taste");

                if (tasteStr != null && !tasteStr.trim().isEmpty()) {
                    String[] tastesArray = parseCommaSeparatedValues(tasteStr);
                    
                    for (String tasteName : tastesArray) {
                        tasteName = tasteName.trim();
                        if (!tasteName.isEmpty()) {
                            Long tasteId = tasteCache.computeIfAbsent(
                                tasteName, 
                                name -> insertAndGetId(jdbcTemplate, "tastes", name)
                            );
                            
                            // Вставляем связь в промежуточную таблицу
                            insertRelation(jdbcTemplate, "hookah_tastes", hookahId, tasteId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error migrating hookah tastes", e);
        }
    }

    private String[] parseCommaSeparatedValues(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new String[0];
        }
        
        return Arrays.stream(input.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new);
    }

    private Map<String, Long> getIngredientCache(JdbcTemplate jdbcTemplate) {
        Map<String, Long> cache = new HashMap<>();
        try {
            String sql = "SELECT id, name FROM ingredients";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : results) {
                cache.put((String) row.get("name"), ((Number) row.get("id")).longValue());
            }
        } catch (DataAccessException e) {
            // Игнорируем ошибку, если таблица еще не существует
        }
        return cache;
    }

    private Map<String, Long> getGenreCache(JdbcTemplate jdbcTemplate) {
        Map<String, Long> cache = new HashMap<>();
        try {
            String sql = "SELECT id, name FROM genres";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : results) {
                cache.put((String) row.get("name"), ((Number) row.get("id")).longValue());
            }
        } catch (DataAccessException e) {
            // Игнорируем ошибку, если таблица еще не существует
        }
        return cache;
    }

    private Map<String, Long> getTasteCache(JdbcTemplate jdbcTemplate) {
        Map<String, Long> cache = new HashMap<>();
        try {
            String sql = "SELECT id, name FROM tastes";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            for (Map<String, Object> row : results) {
                cache.put((String) row.get("name"), ((Number) row.get("id")).longValue());
            }
        } catch (DataAccessException e) {
            // Игнорируем ошибку, если таблица еще не существует
        }
        return cache;
    }

    private Long insertAndGetId(JdbcTemplate jdbcTemplate, String tableName, String name) {
        try {
            String insertSql = "INSERT INTO " + tableName + " (name) VALUES (?)";
            jdbcTemplate.update(insertSql, name);
            
            String selectIdSql = "SELECT id FROM " + tableName + " WHERE name = ?";
            return jdbcTemplate.queryForObject(selectIdSql, Long.class, name);
        } catch (Exception e) {
            // Если произошла ошибка дубликата (в редких случаях конкурентного доступа), 
            // пробуем получить существующий ID
            try {
                String selectIdSql = "SELECT id FROM " + tableName + " WHERE name = ?";
                return jdbcTemplate.queryForObject(selectIdSql, Long.class, name);
            } catch (Exception ex) {
                throw new RuntimeException("Error inserting/getting ID for " + tableName + ": " + name, ex);
            }
        }
    }

    private void insertRelation(JdbcTemplate jdbcTemplate, String tableName, Object id1, Object id2) {
        try {
            String insertSql = "INSERT INTO " + tableName + " SELECT ?, ? WHERE NOT EXISTS (" +
                              "SELECT 1 FROM " + tableName + " WHERE " + 
                              getFirstColumnName(tableName) + " = ? AND " + 
                              getSecondColumnName(tableName) + " = ?)";
            jdbcTemplate.update(insertSql, id1, id2, id1, id2);
        } catch (Exception e) {
            throw new RuntimeException("Error inserting relation for " + tableName, e);
        }
    }

    private String getFirstColumnName(String tableName) {
        switch (tableName) {
            case "food_ingredients": return "food_id";
            case "drink_ingredients": return "drink_id";
            case "game_genres": return "game_id";
            case "hookah_tastes": return "hookah_id";
            default: return "id1"; // резервный вариант
        }
    }

    private String getSecondColumnName(String tableName) {
        switch (tableName) {
            case "food_ingredients": return "ingredient_id";
            case "drink_ingredients": return "ingredient_id";
            case "game_genres": return "genre_id";
            case "hookah_tastes": return "taste_id";
            default: return "id2"; // резервный вариант
        }
    }
}
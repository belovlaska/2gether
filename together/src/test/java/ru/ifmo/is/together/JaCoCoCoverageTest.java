package ru.ifmo.is.together;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Этот тест нужен для генерации отчета JaCoCo о покрытии кода тестами.
 * Отчеты будут сгенерированы в build/reports/jacoco/test/
 */
@SpringBootTest
class JaCoCoCoverageTest {

    @Test
    void contextLoads() {
        // Этот метод просто загружает контекст Spring для обеспечения покрытия
    }
}
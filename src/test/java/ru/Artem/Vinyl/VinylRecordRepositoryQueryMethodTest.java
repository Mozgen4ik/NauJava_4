package ru.Artem.Vinyl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Genre;
import ru.Artem.Vinyl.entity.Label;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.crud_repos.GenreRepository;
import ru.Artem.Vinyl.crud_repos.LabelRepository;

import java.util.List;

/**
 * Тест для проверки Query Method (пункт 8 задания).
 *
 * Проверяет метод findByYearBetweenAndPriceLessThan,
 * который использует ключевые слова Between и And (Query Lookup Strategy).
 */
@SpringBootTest
public class VinylRecordRepositoryQueryMethodTest {

    @Autowired
    private VinylRecordRepository vinylRecordRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Тест Query Method: findByYearBetweenAndPriceLessThan.
     *
     * Создаёт две пластинки:
     * - одну дешёвую (должна попасть в результат)
     * - одну дорогую (не должна попасть в результат)
     *
     * Проверяет, что метод корректно фильтрует по году и цене.
     */
    @Test
    public void testFindByYearBetweenAndPriceLessThan() {
        // Создаём жанр и лейбл для тестовых данных
        Genre genre = new Genre();
        genre.setName("TestGenreA");
        genre.setDescription("Test genre for query method");
        genreRepository.save(genre);

        Label label = new Label();
        label.setName("TestLabelA");
        label.setCountry("UK");
        labelRepository.save(label);

        // Создаём дешёвую пластинку (должна попасть в результат)
        VinylRecord cheapRecord = new VinylRecord();
        cheapRecord.setTitle("Cheap 1975");
        cheapRecord.setYear(1975);
        cheapRecord.setPrice(5.0f);
        cheapRecord.setCondition("Good");
        cheapRecord.setGenre(genre);
        cheapRecord.setLabel(label);
        vinylRecordRepository.save(cheapRecord);

        // Создаём дорогую пластинку (не должна попасть в результат)
        VinylRecord expensiveRecord = new VinylRecord();
        expensiveRecord.setTitle("Expensive 1976");
        expensiveRecord.setYear(1976);
        expensiveRecord.setPrice(50.0f);
        expensiveRecord.setCondition("Mint");
        expensiveRecord.setGenre(genre);
        expensiveRecord.setLabel(label);
        vinylRecordRepository.save(expensiveRecord);

        // Вызываем метод: ищем между 1970 и 1980 годом с ценой менее 10
        List<VinylRecord> found = vinylRecordRepository.findByYearBetweenAndPriceLessThan(1970, 1980, 10.0f);

        // Проверки
        Assertions.assertNotNull(found, "Result should not be null");
        Assertions.assertTrue(
                found.stream().anyMatch(v -> "Cheap 1975".equals(v.getTitle())),
                "Should find cheap record"
        );
        Assertions.assertFalse(
                found.stream().anyMatch(v -> "Expensive 1976".equals(v.getTitle())),
                "Should NOT find expensive record"
        );
    }
}
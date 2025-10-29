package ru.Artem.Vinyl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Genre;
import ru.Artem.Vinyl.entity.Label;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepositoryCustom;
import ru.Artem.Vinyl.crud_repos.GenreRepository;
import ru.Artem.Vinyl.crud_repos.LabelRepository;

import java.util.List;

/**
 * Тесты для проверки методов репозитория VinylRecord (пункт 8 задания).
 *
 * Проверяет:
 * - JPQL метод findByGenreName (поиск через связанную сущность Genre)
 * - Criteria API метод findByGenreNameCriteria (аналогичный запрос)
 *
 * Оба метода должны возвращать одинаковые результаты.
 */
@SpringBootTest
public class VinylRecordRepositoryCriteriaAndJPQLTest {

    @Autowired
    private VinylRecordRepository vinylRecordRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private LabelRepository labelRepository;

    /**
     * Тест для проверки поиска по имени жанра через JPQL и Criteria API.
     * Создаёт тестовые данные и проверяет, что оба метода находят нужную запись.
     */
    @Test
    public void testFindByGenreNameJPQLAndCriteria() {
        // Создаём уникальный жанр для теста
        Genre genre = new Genre();
        genre.setName("UniqueGenreX");
        genre.setDescription("Test genre for JPQL and Criteria");
        genreRepository.save(genre);

        // Создаём лейбл
        Label label = new Label();
        label.setName("LabelX");
        label.setCountry("USA");
        labelRepository.save(label);

        // Создаём виниловую пластинку с этим жанром
        VinylRecord record = new VinylRecord();
        record.setTitle("RecordX");
        record.setYear(2000);
        record.setPrice(20.0f);
        record.setCondition("Mint");
        record.setGenre(genre);
        record.setLabel(label);
        vinylRecordRepository.save(record);

        // JPQL метод: поиск по имени жанра
        List<VinylRecord> jpqlFound = vinylRecordRepository.findByGenreName("UniqueGenreX");
        Assertions.assertFalse(jpqlFound.isEmpty(), "JPQL method should find records");
        Assertions.assertTrue(
                jpqlFound.stream().anyMatch(r -> "RecordX".equals(r.getTitle())),
                "JPQL method should find RecordX"
        );

        // Criteria API метод: поиск по имени жанра
        List<VinylRecord> criteriaFound = vinylRecordRepository.findByGenreNameCriteria("UniqueGenreX");
        Assertions.assertFalse(criteriaFound.isEmpty(), "Criteria method should find records");
        Assertions.assertTrue(
                criteriaFound.stream().anyMatch(r -> "RecordX".equals(r.getTitle())),
                "Criteria method should find RecordX"
        );
    }
}
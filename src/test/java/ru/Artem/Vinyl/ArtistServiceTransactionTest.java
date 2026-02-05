package ru.Artem.Vinyl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.Artem.Vinyl.entity.Artist;
import ru.Artem.Vinyl.entity.Record_Artist;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Genre;
import ru.Artem.Vinyl.entity.Label;
import ru.Artem.Vinyl.crud_repos.ArtistRepository;
import ru.Artem.Vinyl.crud_repos.Record_ArtistRepository;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.crud_repos.GenreRepository;
import ru.Artem.Vinyl.crud_repos.LabelRepository;
import ru.Artem.Vinyl.service.ArtistService;

import java.util.List;
import java.util.Optional;

/**
 * Тесты для проверки транзакционного метода ArtistService (пункт 8 задания).
 *
 * Проверяет:
 * - Позитивный кейс: успешное удаление артиста со связями
 * - Негативный кейс: откат транзакции при возникновении ошибки
 */
@SpringBootTest
public class ArtistServiceTransactionTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VinylRecordRepository vinylRecordRepository;

    @Autowired
    private Record_ArtistRepository recordArtistRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ArtistService artistService;

    /**
     * Позитивный тест: проверяет успешное удаление артиста вместе со связями.
     *
     * Сценарий:
     * 1. Создаём артиста
     * 2. Создаём виниловую пластинку
     * 3. Создаём связь между артистом и пластинкой (Record_Artist)
     * 4. Вызываем транзакционный метод deleteArtistWithLinks
     * 5. Проверяем, что артист и связь удалены из БД
     */
    @Test
    public void testDeleteArtistWithLinks_success() {
        // Создаём жанр и лейбл для виниловой пластинки
        Genre genre = new Genre();
        genre.setName("TestGenreForDelete");
        genreRepository.save(genre);

        Label label = new Label();
        label.setName("TestLabelForDelete");
        labelRepository.save(label);

        // Создаём артиста
        Artist artist = new Artist();
        artist.setName("ArtistForDelete");
        artist.setCountry("UK");
        artistRepository.save(artist);

        // Создаём виниловую пластинку
        VinylRecord record = new VinylRecord();
        record.setTitle("RecordForArtistDelete");
        record.setYear(1990);
        record.setPrice(10.0f);
        record.setCondition("Good");
        record.setGenre(genre);
        record.setLabel(label);
        vinylRecordRepository.save(record);

        // Создаём связь артист-пластинка
        Record_Artist link = new Record_Artist();
        link.setArtist(artist);
        link.setRecord(record);
        recordArtistRepository.save(link);

        // Проверяем, что связь существует
        List<Record_Artist> linksBefore = recordArtistRepository.findByArtist(artist);
        Assertions.assertFalse(linksBefore.isEmpty(), "Link should exist before deletion");

        // Вызываем транзакционный метод удаления
        artistService.deleteArtistWithLinks(artist.getId());

        // Проверяем, что артист удалён
        Optional<Artist> foundArtist = artistRepository.findById(artist.getId());
        Assertions.assertTrue(foundArtist.isEmpty(), "Artist should be deleted");

        // Проверяем, что связи удалены
        List<Record_Artist> linksAfter = recordArtistRepository.findByArtist(artist);
        Assertions.assertTrue(linksAfter.isEmpty(), "Links should be deleted");
    }

    /**
     * Негативный тест: проверяет откат транзакции при возникновении ошибки.
     *
     * Сценарий:
     * 1. Создаём артиста, пластинку и связь (как в позитивном тесте)
     * 2. Вызываем метод deleteArtistWithLinksAndFail, который бросает исключение
     * 3. Проверяем, что транзакция откатилась, и артист со связью остались в БД
     */
    @Test
    public void testDeleteArtistWithLinks_rollbackOnException() {
        // Создаём жанр и лейбл
        Genre genre = new Genre();
        genre.setName("TestGenreForRollback");
        genreRepository.save(genre);

        Label label = new Label();
        label.setName("TestLabelForRollback");
        labelRepository.save(label);

        // Создаём артиста
        Artist artist = new Artist();
        artist.setName("ArtistForRollback");
        artist.setCountry("USA");
        artistRepository.save(artist);

        // Создаём виниловую пластинку
        VinylRecord record = new VinylRecord();
        record.setTitle("RecordForRollback");
        record.setYear(1991);
        record.setPrice(12.0f);
        record.setCondition("Fair");
        record.setGenre(genre);
        record.setLabel(label);
        vinylRecordRepository.save(record);

        // Создаём связь
        Record_Artist link = new Record_Artist();
        link.setArtist(artist);
        link.setRecord(record);
        recordArtistRepository.save(link);

        // Вызываем метод, который бросает исключение
        // Транзакция должна откатиться
        try {
            artistService.deleteArtistWithLinksAndFail(artist.getId());
            Assertions.fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException ex) {
            // Ожидаемое исключение
            Assertions.assertTrue(ex.getMessage().contains("Simulated failure"));
        }

        // После отката транзакции артист должен остаться в БД
        Optional<Artist> foundArtist = artistRepository.findById(artist.getId());
        Assertions.assertTrue(foundArtist.isPresent(), "Artist should NOT be deleted after rollback");

        // Связи также должны остаться в БД
        List<Record_Artist> linksAfter = recordArtistRepository.findByArtist(artist);
        Assertions.assertFalse(linksAfter.isEmpty(), "Links should NOT be deleted after rollback");
    }
}
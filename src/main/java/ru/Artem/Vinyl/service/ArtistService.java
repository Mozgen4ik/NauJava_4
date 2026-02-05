package ru.Artem.Vinyl.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import ru.Artem.Vinyl.crud_repos.ArtistRepository;
import ru.Artem.Vinyl.crud_repos.Record_ArtistRepository;
import ru.Artem.Vinyl.entity.Artist;
import ru.Artem.Vinyl.entity.Record_Artist;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с артистами (Artist).
 *
 * Пункт 7 задания: транзакционный метод deleteArtistWithLinks.
 *
 * Логика: Artist и Record_Artist связаны таким образом, что Record_Artist
 * не может существовать без Artist (связь через внешний ключ artist_id).
 * При удалении артиста необходимо сначала удалить все его связи с пластинками,
 * затем удалить самого артиста. Это должно происходить в одной транзакции,
 * чтобы обеспечить целостность данных.
 *
 * Транзакционность обеспечивается аннотацией @Transactional (Spring).
 * Если в транзакции произойдёт RuntimeException — все изменения будут откатены.
 */
@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final Record_ArtistRepository recordArtistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository,
                         Record_ArtistRepository recordArtistRepository) {
        this.artistRepository = artistRepository;
        this.recordArtistRepository = recordArtistRepository;
    }

    /**
     * Сохраняет или обновляет артиста.
     */
    public Artist save(Artist artist) {
        return artistRepository.save(artist);
    }

    /**
     * Находит артиста по id.
     */
    public Optional<Artist> findById(Long id) {
        return artistRepository.findById(id);
    }

    /**
     * Возвращает всех артистов.
     */
    public Iterable<Artist> findAll() {
        return artistRepository.findAll();
    }

    /**
     * Удаляет артиста по id (без связей) — обычный non-transactional вызов.
     */
    public void deleteById(Long id) {
        artistRepository.deleteById(id);
    }

    /**
     * Транзакционный метод (пункт 7 задания):
     * Удаляет артиста вместе со всеми его связями Record_Artist.
     *
     * Операция выполняется в одной транзакции:
     * 1. Находим все связи Record_Artist для данного артиста
     * 2. Удаляем каждую связь
     * 3. Удаляем самого артиста
     *
     * Если на любом этапе произойдёт ошибка — вся транзакция откатывается,
     * и данные остаются в исходном состоянии (обеспечение ACID-свойств).
     */
    @Transactional
    public void deleteArtistWithLinks(Long artistId) {
        Optional<Artist> opt = artistRepository.findById(artistId);
        if (opt.isEmpty()) {
            // артист не найден — ничего не делаем
            return;
        }
        Artist artist = opt.get();

        // находим все связи артиста с пластинками
        List<Record_Artist> links = recordArtistRepository.findByArtist(artist);

        // удаляем каждую связь
        for (Record_Artist link : links) {
            recordArtistRepository.delete(link);
        }

        // удаляем самого артиста
        artistRepository.deleteById(artistId);
    }

    /**
     * Вспомогательный метод для тестирования отката транзакций:
     * Выполняет те же действия, что и deleteArtistWithLinks,
     * но затем принудительно бросает RuntimeException.
     *
     * Используется в тестах для проверки, что при ошибке транзакция откатывается
     * и все данные остаются в БД (негативный тест).
     */
    @Transactional
    public void deleteArtistWithLinksAndFail(Long artistId) {
        Optional<Artist> opt = artistRepository.findById(artistId);
        if (opt.isEmpty()) {
            return;
        }
        Artist artist = opt.get();
        List<Record_Artist> links = recordArtistRepository.findByArtist(artist);

        for (Record_Artist link : links) {
            recordArtistRepository.delete(link);
        }

        // моделируем ошибку для отката транзакции
        throw new RuntimeException("Simulated failure to test transaction rollback");
    }
}
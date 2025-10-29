package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import ru.Artem.Vinyl.entity.Record_Artist;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Artist;
import java.util.List;

/**
 * CRUD-репозиторий для связующей сущности Record_Artist.
 * Предоставляет методы для поиска связей по артисту или пластинке.
 */
public interface Record_ArtistRepository extends CrudRepository<Record_Artist, Long> {

    /**
     * Находит все связи для конкретной виниловой пластинки.
     */
    List<Record_Artist> findByRecord(VinylRecord record);

    /**
     * Находит все связи для конкретного артиста.
     */
    List<Record_Artist> findByArtist(Artist artist);
}
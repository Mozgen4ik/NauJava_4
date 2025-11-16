package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Artem.Vinyl.entity.Record_Artist;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Artist;
import java.util.List;

/**
 * CRUD-репозиторий для связующей сущности Record_Artist.
 * REST API доступен по пути /recordArtists
 */
@RepositoryRestResource(path = "recordArtists")
public interface Record_ArtistRepository extends CrudRepository<Record_Artist, Long> {

    List<Record_Artist> findByRecord(VinylRecord record);

    List<Record_Artist> findByArtist(Artist artist);
}
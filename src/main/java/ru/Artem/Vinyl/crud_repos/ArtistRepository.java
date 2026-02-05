package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Artem.Vinyl.entity.Artist;
import java.util.List;

/**
 * CRUD-репозиторий для Artist на основе CrudRepository.
 * REST API доступен по пути /artists
 */
@RepositoryRestResource(path = "artists")
public interface ArtistRepository extends CrudRepository<Artist, Long> {
    // пример метода поиска по имени артиста
    List<Artist> findByNameContaining(String name);
}
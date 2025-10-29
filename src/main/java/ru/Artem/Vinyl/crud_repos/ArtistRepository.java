package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import ru.Artem.Vinyl.entity.Artist;
import java.util.List;

/**
 * CRUD-репозиторий для Artist на основе CrudRepository.
 */
public interface ArtistRepository extends CrudRepository<Artist, Long> {
    // пример метода поиска по имени артиста
    List<Artist> findByNameContaining(String name);
}

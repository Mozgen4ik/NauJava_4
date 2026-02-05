package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Artem.Vinyl.entity.Genre;
import java.util.Optional;

/**
 * CRUD-репозиторий для Genre.
 * REST API доступен по пути /genres
 */
@RepositoryRestResource(path = "genres")
public interface GenreRepository extends CrudRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
}
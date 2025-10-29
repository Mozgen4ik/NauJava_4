package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import ru.Artem.Vinyl.entity.Genre;
import java.util.Optional;

/**
 * CRUD-репозиторий для Genre.
 */
public interface GenreRepository extends CrudRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
}

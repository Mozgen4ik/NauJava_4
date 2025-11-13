package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Artem.Vinyl.entity.Label;
import java.util.Optional;

/**
 * CRUD-репозиторий для Label.
 * REST API доступен по пути /labels
 */
@RepositoryRestResource(path = "labels")
public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);
}
package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import ru.Artem.Vinyl.entity.Label;
import java.util.Optional;

/**
 * CRUD-репозиторий для Label.
 */
public interface LabelRepository extends CrudRepository<Label, Long> {
    Optional<Label> findByName(String name);
}

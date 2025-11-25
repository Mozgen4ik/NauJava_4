package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.Artem.Vinyl.entity.User;
import java.util.Optional;

/**
 * CRUD-репозиторий для User.
 */
@RepositoryRestResource(path = "users")
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Находит пользователя по имени (username) для аутентификации.
     */
    Optional<User> findByUsername(String username);
}
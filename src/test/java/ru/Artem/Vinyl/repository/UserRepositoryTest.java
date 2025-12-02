package ru.Artem.Vinyl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.entity.Role;
import ru.Artem.Vinyl.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для UserRepository.
 *
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    /**
     * ТЕСТ: Сохранение и поиск пользователя.
     */
    @Test
    void whenSaveUser_thenFindByUsername() {
        // Arrange - создаём пользователя
        User user = new User("testuser", "password");
        user.addRole(Role.USER);

        // Act - сохраняем через EntityManager
        entityManager.persist(user);
        entityManager.flush();

        // Assert - ищем через репозиторий
        Optional<User> found = userRepository.findByUsername("testuser");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        assertTrue(found.get().getRoles().contains(Role.USER));
    }

    /**
     * ТЕСТ: Поиск несуществующего пользователя.
     */
    @Test
    void whenFindByUsername_NotExist_thenReturnEmpty() {
        // Act
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Assert
        assertFalse(found.isPresent());
    }

    /**
     * ТЕСТ: Сохранение пользователя с несколькими ролями.
     */
    @Test
    void whenSaveUserWithMultipleRoles_thenRolesArePersisted() {
        // Arrange
        User admin = new User("admin", "adminpass");
        admin.addRole(Role.ADMIN);
        admin.addRole(Role.USER);

        // Act
        entityManager.persist(admin);
        entityManager.flush();

        // Assert
        Optional<User> found = userRepository.findById(admin.getId());
        assertTrue(found.isPresent());
        assertEquals(2, found.get().getRoles().size());
        assertTrue(found.get().getRoles().contains(Role.ADMIN));
        assertTrue(found.get().getRoles().contains(Role.USER));
    }

    /**
     * ТЕСТ: Уникальность username.
     */
    @Test
    void whenSaveTwoUsersWithSameUsername_thenException() {
        // Arrange
        User user1 = new User("sameuser", "password1");
        User user2 = new User("sameuser", "password2");

        // Act & Assert
        entityManager.persist(user1);

        // Попытка сохранить второго пользователя с тем же username
        // должна вызвать исключение при flush
        assertThrows(Exception.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }

    /**
     * ТЕСТ: Удаление пользователя.
     */
    @Test
    void whenDeleteUser_thenUserNotFound() {
        // Arrange - создаём и сохраняем пользователя
        User user = new User("todelete", "password");
        entityManager.persist(user);
        entityManager.flush();

        Long userId = user.getId();

        // Act - удаляем
        userRepository.deleteById(userId);

        // Assert - проверяем, что пользователь удалён
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent());
    }

    /**
     * ТЕСТ: Подсчёт количества пользователей.
     */
    @Test
    void whenSaveMultipleUsers_thenCountIsCorrect() {
        // Arrange - создаём 3 пользователей
        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");
        User user3 = new User("user3", "pass3");

        // Act
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // Assert
        long count = userRepository.count();
        assertEquals(3, count);
    }
}
package ru.Artem.Vinyl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.entity.Role;
import ru.Artem.Vinyl.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для UserService.
 *
 * Использует Mockito для создания mock-объектов репозитория и encoder'а.
 * Проверяет положительные и негативные сценарии работы сервиса.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // Mock-объект репозитория (не настоящий, а "пустышка")
    @Mock
    private UserRepository userRepository;

    // Mock-объект для шифрования паролей
    @Mock
    private PasswordEncoder passwordEncoder;

    // Тестируемый сервис, в который будут внедрены mock-объекты
    @InjectMocks
    private UserService userService;

    private User testUser;

    /**
     * Метод выполняется перед каждым тестом.
     * Подготавливает тестовые данные.
     */
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123");
        testUser.setId(1L);
        testUser.addRole(Role.USER);
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешный поиск пользователя по имени.
     */
    @Test
    void findByUsername_Success() {
        // Arrange (Подготовка)
        // Настраиваем mock: когда вызовется findByUsername, вернуть testUser
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(testUser));

        // Act (Действие)
        Optional<User> result = userService.findByUsername("testuser");

        // Assert (Проверка)
        assertTrue(result.isPresent(), "Пользователь должен быть найден");
        assertEquals("testuser", result.get().getUsername());

        // Проверяем, что метод репозитория был вызван ровно 1 раз
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Пользователь не найден.
     */
    @Test
    void findByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent"))
                .thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent(), "Пользователь не должен быть найден");
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешное добавление нового пользователя.
     */
    @Test
    void addUser_Success() {
        // Arrange
        User newUser = new User("newuser", "password");

        // Настраиваем mock: пользователя с таким именем нет
        when(userRepository.findByUsername("newuser"))
                .thenReturn(Optional.empty());

        // Настраиваем mock: encoder возвращает зашифрованный пароль
        when(passwordEncoder.encode("password"))
                .thenReturn("$2a$10$encodedPassword");

        // Настраиваем mock: save возвращает сохранённого пользователя
        when(userRepository.save(any(User.class)))
                .thenReturn(newUser);

        // Act
        userService.addUser(newUser);

        // Assert
        // Проверяем, что пароль был зашифрован
        verify(passwordEncoder, times(1)).encode("password");

        // Проверяем, что пользователь был сохранён
        verify(userRepository, times(1)).save(any(User.class));

        // Проверяем, что пользователю была назначена роль USER
        assertTrue(newUser.getRoles().contains(Role.USER));
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Попытка добавить пользователя с существующим именем.
     * Должно выброситься исключение IllegalArgumentException.
     */
    @Test
    void addUser_UserAlreadyExists_ThrowsException() {
        // Arrange
        User existingUser = new User("existinguser", "password");

        // Настраиваем mock: пользователь с таким именем уже существует
        when(userRepository.findByUsername("existinguser"))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        // Проверяем, что выбрасывается исключение
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.addUser(existingUser),
                "Должно быть выброшено исключение IllegalArgumentException"
        );

        // Проверяем сообщение исключения
        assertTrue(exception.getMessage().contains("already exists"));

        // Проверяем, что save НЕ был вызван
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешное сохранение пользователя.
     */
    @Test
    void save_Success() {
        // Arrange
        when(userRepository.save(testUser)).thenReturn(testUser);

        // Act
        User result = userService.save(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    /**
     * ТЕСТ: Проверка, что новому пользователю назначается роль USER по умолчанию.
     */
    @Test
    void addUser_AssignsDefaultRole() {
        // Arrange
        User userWithoutRoles = new User("newuser", "password");
        // У пользователя изначально нет ролей
        assertTrue(userWithoutRoles.getRoles().isEmpty());

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(userWithoutRoles);

        // Act
        userService.addUser(userWithoutRoles);

        // Assert
        // После добавления должна быть роль USER
        assertTrue(userWithoutRoles.getRoles().contains(Role.USER));
        assertEquals(1, userWithoutRoles.getRoles().size());
    }

    /**
     * ТЕСТ: Проверка, что пароль действительно шифруется.
     */
    @Test
    void addUser_PasswordIsEncoded() {
        // Arrange
        User newUser = new User("testuser", "plainPassword");
        String encodedPassword = "$2a$10$encodedPassword";

        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            // Проверяем, что пароль изменился на зашифрованный
            assertEquals(encodedPassword, savedUser.getPassword());
            return savedUser;
        });

        // Act
        userService.addUser(newUser);

        // Assert
        verify(passwordEncoder, times(1)).encode("plainPassword");
    }
}
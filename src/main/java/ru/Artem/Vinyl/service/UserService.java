package ru.Artem.Vinyl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.entity.Role;
import ru.Artem.Vinyl.entity.User;

import java.util.Optional;

/**
 * Сервис для работы с пользователями приложения.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Находит пользователя по имени.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Добавляет нового пользователя в систему.
     * Пароль шифруется перед сохранением.
     * По умолчанию новому пользователю назначается роль USER.
     */
    public void addUser(User user) {
        // Проверяем, существует ли уже пользователь с таким именем
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("User with username '" + user.getUsername() + "' already exists");
        }

        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Если роли не заданы, назначаем роль USER по умолчанию
        if (user.getRoles().isEmpty()) {
            user.addRole(Role.USER);
        }

        // Сохраняем пользователя в БД
        userRepository.save(user);
    }

    /**
     * Сохраняем пользователя (для обновления данных).
     */
    public User save(User user) {
        return userRepository.save(user);
    }
}
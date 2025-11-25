package ru.Artem.Vinyl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.entity.Role;
import ru.Artem.Vinyl.entity.User;

/**
 * Инициализатор данных.
 * Создание тестовых пользователей при первом запуске приложения.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Создаём админа, если его ещё нет
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", passwordEncoder.encode("admin123"));
            admin.addRole(Role.ADMIN);
            admin.addRole(Role.USER);
            userRepository.save(admin);
            System.out.println("✓ Создан тестовый администратор: admin / admin123");
        }

        // Создаём обычного пользователя, если его ещё нет
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User("user", passwordEncoder.encode("user123"));
            user.addRole(Role.USER);
            userRepository.save(user);
            System.out.println("✓ Создан тестовый пользователь: user / user123");
        }
    }
}
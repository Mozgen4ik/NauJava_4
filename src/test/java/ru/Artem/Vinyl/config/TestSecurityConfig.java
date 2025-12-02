package ru.Artem.Vinyl.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Тестовая конфигурация Spring Security.
 * не для prod версий
 */
@TestConfiguration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    /**
     * Упрощённая конфигурация безопасности для тестов.
     * (позволяет доступ ко всем endpoint'ам без аутентификации.)
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // отключаем CSRF для тестов
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // разрешаем все запросы
                );

        return http.build();
    }
}
package ru.Artem.Vinyl.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Настройка кодировщика паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настройка цепочки фильтров безопасности (SecurityFilterChain).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Настройка правил доступа
                .authorizeHttpRequests((requests) -> requests
                        // Эти пути доступны всем (без аутентификации)
                        .requestMatchers("/", "/registration", "/login", "/error").permitAll()

                        // Swagger UI доступен только администраторам
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").hasRole("ADMIN")

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )

                // Настройка формы логина
                .formLogin((form) -> form
                        .loginPage("/login")           // наша кастомная страница логина
                        .defaultSuccessUrl("/vinyl/view/list", true)  // куда перенаправлять после успешного входа
                        .permitAll()                   // страница логина доступна всем
                )

                // Настройка выхода из системы
                .logout((logout) -> logout
                        .logoutUrl("/logout")          // URL для выхода
                        .logoutSuccessUrl("/login?logout")  // куда перенаправлять после выхода
                        .permitAll()                   // выход доступен всем
                );

        return http.build();
    }
}
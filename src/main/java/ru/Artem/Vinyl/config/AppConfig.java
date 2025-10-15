package ru.Artem.Vinyl.config;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    public void init() {
        // Выводим имя и версию после инициализации бина
        System.out.println("Приложение \"" + appName + "\" запущено. Версия: " + appVersion);
    }
}
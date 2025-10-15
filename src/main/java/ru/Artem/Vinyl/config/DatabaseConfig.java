package ru.Artem.Vinyl.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.Artem.Vinyl.entity.VinylRecord;

@Configuration
public class DatabaseConfig {

    @Bean
    public List<VinylRecord> vinylRecordList() {
        // Возвращаем новый список для хранения объектов VinylRecord.
        return new ArrayList<>();
    }
}
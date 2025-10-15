package ru.Artem.Vinyl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.Artem.Vinyl.dao.VinylRecordRepository;
import ru.Artem.Vinyl.entity.VinylRecord;

import java.util.List;

@Service
public class VinylRecordServiceImpl implements VinylRecordService {

    private final VinylRecordRepository repository;

    @Autowired
    public VinylRecordServiceImpl(VinylRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addRecord(Long id, String artist, String title, int year, String country, String style) {
        VinylRecord record = new VinylRecord();
        record.setId(id);
        record.setArtist(artist);
        record.setTitle(title);
        record.setYear(year);
        record.setCountry(country);
        record.setStyle(style);
        repository.create(record);
    }

    @Override
    public VinylRecord getRecord(Long id) {
        return repository.read(id);
    }

    @Override
    public void updateRecord(Long id, String artist, String title, int year, String country, String style) {
        VinylRecord record = new VinylRecord();
        record.setId(id);
        record.setArtist(artist);
        record.setTitle(title);
        record.setYear(year);
        record.setCountry(country);
        record.setStyle(style);
        repository.update(record);
    }

    @Override
    public void deleteRecord(Long id) {
        repository.delete(id);
    }

    @Override
    public List<VinylRecord> listAllRecords() {
        // Для упрощения возвращаем внутренний список из репозитория
        // (не безопасно для продакшена, но подходит для учебного примера)
        return repository.readAll();
    }
}
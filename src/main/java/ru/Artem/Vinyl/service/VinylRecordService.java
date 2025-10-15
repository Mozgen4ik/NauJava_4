package ru.Artem.Vinyl.service;

import ru.Artem.Vinyl.entity.VinylRecord;
import java.util.List;

public interface VinylRecordService {
    void addRecord(Long id, String artist, String title, int year, String country, String style);
    VinylRecord getRecord(Long id);
    void updateRecord(Long id, String artist, String title, int year, String country, String style);
    void deleteRecord(Long id);
    List<VinylRecord> listAllRecords();
}
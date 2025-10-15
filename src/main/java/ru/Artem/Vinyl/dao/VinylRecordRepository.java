package ru.Artem.Vinyl.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.Artem.Vinyl.entity.VinylRecord;

@Component
public class VinylRecordRepository implements CrudRepository<VinylRecord, Long> {

    private final List<VinylRecord> vinylRecordList;

    @Autowired
    public VinylRecordRepository(List<VinylRecord> vinylRecordList) {
        this.vinylRecordList = vinylRecordList;
    }

    @Override
    public void create(VinylRecord record) {
        vinylRecordList.add(record);
    }

    @Override
    public VinylRecord read(Long id) {
        return vinylRecordList.stream()
                .filter(record -> record.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(VinylRecord record) {
        for (int i = 0; i < vinylRecordList.size(); i++) {
            if (vinylRecordList.get(i).getId().equals(record.getId())) {
                vinylRecordList.set(i, record);
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        vinylRecordList.removeIf(record -> record.getId().equals(id));
    }

    @Override
    public List<VinylRecord> readAll() {
        // Просто возвращаем текущий список записей
        return vinylRecordList;
    }
}
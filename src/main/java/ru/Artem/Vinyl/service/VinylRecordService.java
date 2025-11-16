package ru.Artem.Vinyl.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepositoryCustom;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для бизнес-логики, связанной с VinylRecord.
 *
 * Предоставляет:
 *  - базовые операции сохранения/поиска (save, findById, findAll)
 *  - методы, использующие Query Lookup Strategy (репозиторий): findByYearBetweenAndPriceLessThan
 *  - метод поиска по названию жанра через JPQL: findByGenreName
 *  - методы-обёртки над Criteria-реализацией (через VinylRecordRepositoryCustom),
 *    чтобы использовать альтернативную реализацию запросов (пункт 6 методички).
 */
@Service
public class VinylRecordService {

    private final VinylRecordRepository vinylRecordRepository;


    @Autowired
    public VinylRecordService(VinylRecordRepository vinylRecordRepository) {
        this.vinylRecordRepository = vinylRecordRepository;
    }

    /**
     * Сохраняет или обновляет VinylRecord.
     */
    public VinylRecord save(VinylRecord record) {
        return vinylRecordRepository.save(record);
    }

    /**
     * Находит VinylRecord по id.
     */
    public Optional<VinylRecord> findById(Long id) {
        return vinylRecordRepository.findById(id);
    }

    /**
     * Возвращает все VinylRecord.
     */
    public Iterable<VinylRecord> findAll() {
        return vinylRecordRepository.findAll();
    }

    /**
     * Поиск по части названия (использует метод-lookup в репозитории).
     */
    public List<VinylRecord> findByTitleFragment(String fragment) {
        return vinylRecordRepository.findByTitleContaining(fragment);
    }

    /**
     * Пример использования Query Lookup Strategy
     */
    public List<VinylRecord> findByYearBetweenAndPriceLessThan(Integer startYear, Integer endYear, Float maxPrice) {
        return vinylRecordRepository.findByYearBetweenAndPriceLessThan(startYear, endYear, maxPrice);
    }

    /**
     * JPQL-метод: найти записи по имени жанра
     */
    public List<VinylRecord> findByGenreName(String genreName) {
        return vinylRecordRepository.findByGenreName(genreName);
    }

    /**
     * Criteria-реализация того же поиска: year between & price less than.
     */
    public List<VinylRecord> findByYearBetweenAndPriceLessThanCriteria(Integer startYear, Integer endYear, Float maxPrice) {
        return vinylRecordRepository.findByYearBetweenAndPriceLessThanCriteria(startYear, endYear, maxPrice);
    }

    /**
     * Criteria-реализация поиска по имени жанра.
     */
    public List<VinylRecord> findByGenreNameCriteria(String genreName) {
        return vinylRecordRepository.findByGenreNameCriteria(genreName);
    }
}

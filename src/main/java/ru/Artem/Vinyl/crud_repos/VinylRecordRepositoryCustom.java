package ru.Artem.Vinyl.crud_repos;

import ru.Artem.Vinyl.entity.VinylRecord;
import java.util.List;

/**
 * Кастомный интерфейс для методов, реализованных через Criteria API.
 * Здесь мы описываем методы, которые будут реализованы в VinylRecordRepositoryImpl.
 */
public interface VinylRecordRepositoryCustom {
    List<VinylRecord> findByYearBetweenAndPriceLessThanCriteria(Integer startYear, Integer endYear, Float maxPrice);
    List<VinylRecord> findByGenreNameCriteria(String genreName);
}

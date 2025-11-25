package ru.Artem.Vinyl.crud_repos;

import ru.Artem.Vinyl.entity.VinylRecord;
import java.util.List;

/**
 * Кастомный интерфейс для методов через Criteria API.
 */
public interface VinylRecordRepositoryCustom {
    List<VinylRecord> findByYearBetweenAndPriceLessThanCriteria(Integer startYear, Integer endYear, Float maxPrice);
    List<VinylRecord> findByGenreNameCriteria(String genreName);
}

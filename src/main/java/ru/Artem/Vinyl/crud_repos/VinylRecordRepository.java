package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.Artem.Vinyl.entity.VinylRecord;
import java.util.List;

/**
 * CRUD-репозиторий для VinylRecord.
 *
 * Реализует стандартные CRUD-операции через CrudRepository.
 *
 * Пункт 5 задания:
 * - Метод с Query Lookup Strategy (Between и And): findByYearBetweenAndPriceLessThan
 * - Метод с JPQL для поиска через связанную сущность (Genre): findByGenreName
 */
public interface VinylRecordRepository extends CrudRepository<VinylRecord, Long>, VinylRecordRepositoryCustom {

    /**
     * Query Lookup Strategy: найти записи с year BETWEEN startYear AND endYear И price < maxPrice.
     * Использует ключевые слова Between и And (требование из пункта 5).
     */
    List<VinylRecord> findByYearBetweenAndPriceLessThan(Integer startYear, Integer endYear, Float maxPrice);

    /**
     * JPQL query через связанную сущность (Genre).
     * Поиск виниловых пластинок по названию жанра (требование из пункта 5).
     */
    @Query("SELECT v FROM VinylRecord v WHERE v.genre.name = :genreName")
    List<VinylRecord> findByGenreName(@Param("genreName") String genreName);

    /**
     * Простой поиск по части названия (дополнительный метод для удобства).
     */
    List<VinylRecord> findByTitleContaining(String fragment);
}
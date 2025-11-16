package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.Artem.Vinyl.entity.VinylRecord;
import java.util.List;

/**
 * CRUD-репозиторий для VinylRecord.
 * REST API доступен по пути /vinylRecords
 */
@RepositoryRestResource(path = "vinylRecords")
public interface VinylRecordRepository extends CrudRepository<VinylRecord, Long>, VinylRecordRepositoryCustom {

    List<VinylRecord> findByYearBetweenAndPriceLessThan(Integer startYear, Integer endYear, Float maxPrice);

    /**
     * Поиск виниловых пластинок по названию жанра
     */
    @Query("SELECT v FROM VinylRecord v WHERE v.genre.name = :genreName")
    List<VinylRecord> findByGenreName(@Param("genreName") String genreName);

    /**
     * Простой поиск по части названия
     */
    List<VinylRecord> findByTitleContaining(String fragment);
}
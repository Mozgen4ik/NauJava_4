package ru.Artem.Vinyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.service.VinylRecordService;

import java.util.List;

/**
 * REST контроллер для работы с виниловыми пластинками.
 * Все методы доступны по пути: /api/vinyl
 */
@RestController
@RequestMapping("/api/vinyl")
public class VinylRecordController {

    private final VinylRecordService vinylRecordService;

    @Autowired
    public VinylRecordController(VinylRecordService vinylRecordService) {
        this.vinylRecordService = vinylRecordService;
    }

    /**
     * Поиск пластинок по диапазону годов и максимальной цене.
     *
     *
     * @param startYear начальный год
     * @param endYear конечный год
     * @param maxPrice максимальная цена
     * @return список найденных пластинок
     */
    @GetMapping("/searchByYearAndPrice")
    public List<VinylRecord> searchByYearAndPrice(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear,
            @RequestParam Float maxPrice) {
        return vinylRecordService.findByYearBetweenAndPriceLessThan(startYear, endYear, maxPrice);
    }

    /**
     * Поиск пластинок по названию жанра (JPQL).
     *
     *
     * @param genreName название жанра
     * @return список найденных пластинок
     */
    @GetMapping("/searchByGenre")
    public List<VinylRecord> searchByGenre(@RequestParam String genreName) {
        if (genreName == null || genreName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название жанра не может быть пустым");
        }
        return vinylRecordService.findByGenreName(genreName);
    }

    /**
     * Поиск пластинок по диапазону годов и цене (Criteria API).
     *
     * @param startYear начальный год
     * @param endYear конечный год
     * @param maxPrice максимальная цена
     * @return список найденных пластинок
     */
    @GetMapping("/searchByYearAndPriceCriteria")
    public List<VinylRecord> searchByYearAndPriceCriteria(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear,
            @RequestParam Float maxPrice) {
        return vinylRecordService.findByYearBetweenAndPriceLessThanCriteria(startYear, endYear, maxPrice);
    }

    /**
     * Поиск пластинок по названию жанра (Criteria API).
     *
     *
     * @param genreName название жанра
     * @return список найденных пластинок
     */
    @GetMapping("/searchByGenreCriteria")
    public List<VinylRecord> searchByGenreCriteria(@RequestParam String genreName) {
        return vinylRecordService.findByGenreNameCriteria(genreName);
    }
}
package ru.Artem.Vinyl.crud_repos;

import org.springframework.data.repository.CrudRepository;
import ru.Artem.Vinyl.entity.Report;

/**
 * CRUD-репозиторий для Report (отчётов).
 * Предоставляет стандартные методы для работы с отчётами.
 */
public interface ReportRepository extends CrudRepository<Report, Long> {
    // Стандартные методы CrudRepository достаточно для работы с отчётами
}
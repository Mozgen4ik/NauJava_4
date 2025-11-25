package ru.Artem.Vinyl.entity;

/**
 * Статусы формирования отчёта.
 *
 * CREATED - отчёт создан, формирование началось
 * COMPLETED - отчёт успешно сформирован
 * ERROR - ошибка при формировании
 */
public enum ReportStatus {
    CREATED,     // создан
    COMPLETED,   // завершён
    ERROR        // ошибка
}
package ru.Artem.Vinyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.Artem.Vinyl.entity.Report;
import ru.Artem.Vinyl.entity.ReportStatus;
import ru.Artem.Vinyl.service.ReportService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST контроллер для работы с отчётами.
 *
 * Предоставляет API для:
 * - создания отчёта и запуска его формирования
 * - получения содержимого отчёта по ID
 *
 * Все методы доступны по базовому пути: /api/reports
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Создаёт новый отчёт и запускает его формирование (асинхронно).
     *
     * ВАЖНО: Метод НЕ ждёт завершения формирования отчёта!
     * Он сразу возвращает ID отчёта, а формирование происходит в фоне.
     *
     * Пример запроса:
     * POST /api/reports/generate
     *
     * Пример ответа:
     * {
     *   "reportId": 1,
     *   "status": "CREATED",
     *   "message": "Отчёт создан. Формирование началось."
     * }
     *
     * @return ResponseEntity с ID отчёта и статусом
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateReport() {
        // Создаём отчёт в БД
        Long reportId = reportService.createReport();

        // Запускаем асинхронное формирование отчёта
        // Метод НЕ ждёт завершения, выполнение продолжится в фоновом потоке
        reportService.generateReportAsync(reportId);

        // Формируем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("reportId", reportId);
        response.put("status", ReportStatus.CREATED.name());
        response.put("message", "Отчёт создан. Формирование началось. Используйте GET /api/reports/" + reportId + " для получения результата.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
/**
 * Получает содержимое отчёта по его ID.
 *
 * Возможные сценарии:
 * 1. Отчёт ещё формируется (статус CREATED) - возвращает сообщение "В процессе"
 * 2. Отчёт готов (статус COMPLETED) - возвращает HTML-содержимое отчёта
 * 3. Ошибка при формировании (статус ERROR) - возвращает сообщение об ошибке
 * 4. Отчёт не найден - возвращает 404
 *
 * Пример запроса:
 * GET /api/reports/1
 *
 * @param reportId ID отчёта
 * @return ResponseEntity с содержимым отчёта или информацией о статусе
 */
@GetMapping("/{reportId}")
public ResponseEntity<?> getReport(@PathVariable Long reportId) {
    Optional<Report> reportOpt = reportService.getReport(reportId);

    if (reportOpt.isEmpty()) {
        // Отчёт не найден
        Map<String, String> error = new HashMap<>();
        error.put("error", "Отчёт с ID " + reportId + " не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Report report = reportOpt.get();

    // Проверяем статус отчёта
    switch (report.getStatus()) {
        case CREATED:
            // Отчёт ещё формируется
            Map<String, String> inProgress = new HashMap<>();
            inProgress.put("reportId", reportId.toString());
            inProgress.put("status", ReportStatus.CREATED.name());
            inProgress.put("message", "Отчёт в процессе формирования. Попробуйте позже.");
            return ResponseEntity.ok(inProgress);

        case ERROR:
            // Ошибка при формировании
            Map<String, String> error = new HashMap<>();
            error.put("reportId", reportId.toString());
            error.put("status", ReportStatus.ERROR.name());
            error.put("message", "При формировании отчёта произошла ошибка");
            error.put("details", report.getContent());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);

        case COMPLETED:
            // Отчёт готов - возвращаем HTML
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(report.getContent());

        default:
            // На всякий случай (не должно произойти)
            Map<String, String> unknown = new HashMap<>();
            unknown.put("error", "Неизвестный статус отчёта");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(unknown);
    }
}

/**
 * Получает статус отчёта (без содержимого).
 *
 * Удобно для проверки, готов ли отчёт.
 *
 * Пример запроса:
 * GET /api/reports/1/status
 *
 * Пример ответа:
 * {
 *   "reportId": 1,
 *   "status": "COMPLETED"
 * }
 *
 * @param reportId ID отчёта
 * @return ResponseEntity со статусом отчёта
 */
@GetMapping("/{reportId}/status")
public ResponseEntity<?> getReportStatus(@PathVariable Long reportId) {
    Optional<Report> reportOpt = reportService.getReport(reportId);

    if (reportOpt.isEmpty()) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Отчёт с ID " + reportId + " не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    Report report = reportOpt.get();

    Map<String, String> response = new HashMap<>();
    response.put("reportId", reportId.toString());
    response.put("status", report.getStatus().name());

    return ResponseEntity.ok(response);
}
}
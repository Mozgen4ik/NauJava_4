package ru.Artem.Vinyl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.Artem.Vinyl.crud_repos.ReportRepository;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.entity.Report;
import ru.Artem.Vinyl.entity.ReportStatus;
import ru.Artem.Vinyl.entity.VinylRecord;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Сервис для работы с отчётами.
 *
 * Содержит методы для:
 * - создания отчёта
 * - асинхронного формирования отчёта (с использованием многопоточности)
 * - получения содержимого отчёта
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final VinylRecordRepository vinylRecordRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         VinylRecordRepository vinylRecordRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.vinylRecordRepository = vinylRecordRepository;
    }

    /**
     * Создаёт новый отчёт в БД со статусом CREATED.
     *
     * @return ID созданного отчёта
     */
    public Long createReport() {
        Report report = new Report();
        report.setStatus(ReportStatus.CREATED);
        Report savedReport = reportRepository.save(report);
        return savedReport.getId();
    }

    /**
     * Получает содержимое отчёта по его ID.
     *
     * @param reportId ID отчёта
     * @return Optional с отчётом, если найден
     */
    public Optional<Report> getReport(Long reportId) {
        return reportRepository.findById(reportId);
    }

    /**
     * АСИНХРОННЫЙ метод формирования отчёта.
     *
     * Использует:
     * - CompletableFuture для асинхронности
     * - Thread для многопоточности (отдельные потоки для подсчёта пользователей и пластинок)
     *
     * Этапы:
     * 1. Запускается в отдельном потоке (CompletableFuture.supplyAsync)
     * 2. Создаёт 2 потока: один считает пользователей, другой получает список пластинок
     * 3. Ждёт завершения обоих потоков (thread.join())
     * 4. Формирует HTML-отчёт с временем выполнения каждого этапа
     * 5. Сохраняет отчёт в БД со статусом COMPLETED или ERROR
     *
     * @param reportId ID отчёта для заполнения
     */
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Запоминаем время начала формирования отчёта
                long startTime = System.currentTimeMillis();

                // Переменные для хранения результатов из потоков
                // Используем AtomicLong, т.к. они безопасны для многопоточности
                AtomicLong userCount = new AtomicLong(0);
                AtomicLong userCalcTime = new AtomicLong(0);

                // StringBuilder для списка пластинок (не thread-safe, но каждый поток пишет в свою переменную)
                StringBuilder vinylListHtml = new StringBuilder();
                AtomicLong vinylCalcTime = new AtomicLong(0);

                // ПОТОК 1: Подсчёт количества пользователей
                Thread userThread = new Thread(() -> {
                    long start = System.currentTimeMillis();

                    // Считаем пользователей
                    long count = userRepository.count();
                    userCount.set(count);

                    // Считаем время выполнения
                    long elapsed = System.currentTimeMillis() - start;
                    userCalcTime.set(elapsed);

                    System.out.println("Поток 1 (пользователи): выполнен за " + elapsed + " мс");
                });

                // ПОТОК 2: Получение списка виниловых пластинок
                Thread vinylThread = new Thread(() -> {
                    long start = System.currentTimeMillis();

                    // Получаем все пластинки
                    Iterable<VinylRecord> records = vinylRecordRepository.findAll();

                    // Формируем HTML-таблицу
                    StringBuilder html = new StringBuilder();
                    html.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
                    html.append("<tr style='background-color: #4CAF50; color: white;'>");
                    html.append("<th>ID</th><th>Название</th><th>Год</th><th>Цена</th><th>Жанр</th><th>Лейбл</th>");
                    html.append("</tr>");

                    for (VinylRecord record : records) {
                        html.append("<tr>");
                        html.append("<td>").append(record.getId()).append("</td>");
                        html.append("<td>").append(record.getTitle()).append("</td>");
                        html.append("<td>").append(record.getYear()).append("</td>");
                        html.append("<td>").append(record.getPrice()).append("</td>");
                        html.append("<td>").append(record.getGenre() != null ? record.getGenre().getName() : "N/A").append("</td>");
                        html.append("<td>").append(record.getLabel() != null ? record.getLabel().getName() : "N/A").append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</table>");

                    vinylListHtml.append(html);

                    // Считаем время выполнения
                    long elapsed = System.currentTimeMillis() - start;
                    vinylCalcTime.set(elapsed);

                    System.out.println("Поток 2 (пластинки): выполнен за " + elapsed + " мс");
                });

                // Запускаем оба потока
                userThread.start();
                vinylThread.start();

                // ВАЖНО: Ждём завершения обоих потоков
                userThread.join();
                vinylThread.join();

                // Считаем общее время
                long totalTime = System.currentTimeMillis() - startTime;

                // Формируем итоговый HTML-отчёт
                String htmlReport = buildHtmlReport(
                        userCount.get(),
                        userCalcTime.get(),
                        vinylListHtml.toString(),
                        vinylCalcTime.get(),
                        totalTime
                );

                // Сохраняем отчёт в БД
                Optional<Report> reportOpt = reportRepository.findById(reportId);
                if (reportOpt.isPresent()) {
                    Report report = reportOpt.get();
                    report.setContent(htmlReport);
                    report.setStatus(ReportStatus.COMPLETED);
                    reportRepository.save(report);

                    System.out.println("✓ Отчёт #" + reportId + " успешно сформирован за " + totalTime + " мс");
                }

            } catch (Exception e) {
                // При ошибке сохраняем статус ERROR
                System.err.println("✗ Ошибка при формировании отчёта #" + reportId + ": " + e.getMessage());
                e.printStackTrace();

                Optional<Report> reportOpt = reportRepository.findById(reportId);
                if (reportOpt.isPresent()) {
                    Report report = reportOpt.get();
                    report.setStatus(ReportStatus.ERROR);
                    report.setContent("Ошибка при формировании отчёта: " + e.getMessage());
                    reportRepository.save(report);
                }
            }
            return null;
        });
    }

    /**
     * Формирует HTML-отчёт с таблицей статистики.
     */
    private String buildHtmlReport(long userCount, long userCalcTime,
                                   String vinylListHtml, long vinylCalcTime,
                                   long totalTime) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Отчёт - Vinyl Store</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; }");
        html.append("h2 { color: #555; margin-top: 30px; }");
        html.append(".stat-table { border-collapse: collapse; margin: 20px 0; }");
        html.append(".stat-table th { background-color: #2196F3; color: white; padding: 10px; text-align: left; }");
        html.append(".stat-table td { border: 1px solid #ddd; padding: 10px; }");
        html.append("table { margin: 20px 0; }");
        html.append("th { padding: 10px; text-align: left; }");
        html.append("td { padding: 8px; }");
        html.append(".time-info { background-color: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<h1>📊 Отчёт Vinyl Store</h1>");
        html.append("<p>Дата формирования: ").append(new java.util.Date()).append("</p>");

        // Таблица статистики
        html.append("<h2>Статистика</h2>");
        html.append("<table class='stat-table'>");
        html.append("<tr><th>Показатель</th><th>Значение</th></tr>");
        html.append("<tr><td>Количество зарегистрированных пользователей</td><td><strong>")
                .append(userCount).append("</strong></td></tr>");
        html.append("</table>");

        // Список виниловых пластинок
        html.append("<h2>Список виниловых пластинок</h2>");
        html.append(vinylListHtml);

        // Информация о времени выполнения
        html.append("<div class='time-info'>");
        html.append("<h2>⏱ Время выполнения</h2>");
        html.append("<p><strong>Подсчёт пользователей:</strong> ").append(userCalcTime).append(" мс</p>");
        html.append("<p><strong>Получение списка пластинок:</strong> ").append(vinylCalcTime).append(" мс</p>");
        html.append("<p><strong>Общее время формирования отчёта:</strong> <strong>").append(totalTime).append(" мс</strong></p>");
        html.append("</div>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}
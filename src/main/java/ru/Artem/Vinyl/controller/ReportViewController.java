package ru.Artem.Vinyl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.Artem.Vinyl.entity.Report;
import ru.Artem.Vinyl.entity.ReportStatus;
import ru.Artem.Vinyl.service.ReportService;

import java.util.Optional;

/**
 * Контроллер для отображения HTML-страниц с отчётами.
 *
 * Предоставляет удобный веб-интерфейс для работы с отчётами.
 */
@Controller
@RequestMapping("/reports")
public class ReportViewController {

    private final ReportService reportService;

    @Autowired
    public ReportViewController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Страница для создания нового отчёта.
     *
     * GET /reports/create
     */
    @GetMapping("/create")
    public String showCreateReportPage() {
        return "reportCreate";
    }

    /**
     * Создаёт отчёт и перенаправляет на страницу просмотра статуса.
     *
     * GET /reports/generate
     */
    @GetMapping("/generate")
    public String generateReport(Model model) {
        Long reportId = reportService.createReport();
        reportService.generateReportAsync(reportId);

        return "redirect:/reports/" + reportId;
    }

    /**
     * Отображает статус и содержимое отчёта.
     *
     * GET /reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public String viewReport(@PathVariable Long reportId, Model model) {
        Optional<Report> reportOpt = reportService.getReport(reportId);

        if (reportOpt.isEmpty()) {
            model.addAttribute("error", "Отчёт не найден");
            return "reportError";
        }

        Report report = reportOpt.get();
        model.addAttribute("report", report);

        // В зависимости от статуса показываем разные страницы
        if (report.getStatus() == ReportStatus.CREATED) {
            return "reportInProgress";
        } else if (report.getStatus() == ReportStatus.ERROR) {
            return "reportError";
        } else {
            return "reportView";
        }
    }
}
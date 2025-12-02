package ru.Artem.Vinyl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.Artem.Vinyl.crud_repos.ReportRepository;
import ru.Artem.Vinyl.crud_repos.UserRepository;
import ru.Artem.Vinyl.crud_repos.VinylRecordRepository;
import ru.Artem.Vinyl.entity.Report;
import ru.Artem.Vinyl.entity.ReportStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для ReportService.
 *
 * Проверяет создание отчётов и получение их по ID.
 * Асинхронный метод generateReportAsync сложнее тестировать,
 * поэтому для него лучше использовать интеграционные тесты.
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VinylRecordRepository vinylRecordRepository;

    @InjectMocks
    private ReportService reportService;

    private Report testReport;

    @BeforeEach
    void setUp() {
        testReport = new Report();
        testReport.setId(1L);
        testReport.setStatus(ReportStatus.CREATED);
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешное создание отчёта.
     */
    @Test
    void createReport_Success() {
        // Arrange
        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report report = invocation.getArgument(0);
                    report.setId(1L);
                    return report;
                });

        // Act
        Long reportId = reportService.createReport();

        // Assert
        assertNotNull(reportId, "ID отчёта не должен быть null");
        assertEquals(1L, reportId);

        // Проверяем, что save был вызван с отчётом со статусом CREATED
        verify(reportRepository, times(1)).save(argThat(report ->
                report.getStatus() == ReportStatus.CREATED
        ));
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешное получение отчёта по ID.
     */
    @Test
    void getReport_Success() {
        // Arrange
        when(reportRepository.findById(1L))
                .thenReturn(Optional.of(testReport));

        // Act
        Optional<Report> result = reportService.getReport(1L);

        // Assert
        assertTrue(result.isPresent(), "Отчёт должен быть найден");
        assertEquals(1L, result.get().getId());
        assertEquals(ReportStatus.CREATED, result.get().getStatus());

        verify(reportRepository, times(1)).findById(1L);
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Отчёт не найден.
     */
    @Test
    void getReport_NotFound() {
        // Arrange
        when(reportRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act
        Optional<Report> result = reportService.getReport(999L);

        // Assert
        assertFalse(result.isPresent(), "Отчёт не должен быть найден");
        verify(reportRepository, times(1)).findById(999L);
    }

    /**
     * ТЕСТ: Проверка, что новый отчёт создаётся со статусом CREATED.
     */
    @Test
    void createReport_InitialStatusIsCreated() {
        // Arrange
        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report report = invocation.getArgument(0);
                    // Проверяем начальный статус
                    assertEquals(ReportStatus.CREATED, report.getStatus(),
                            "Начальный статус должен быть CREATED");
                    report.setId(1L);
                    return report;
                });

        // Act
        reportService.createReport();

        // Assert
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    /**
     * ТЕСТ: Проверка множественного создания отчётов.
     */
    @Test
    void createReport_MultipleReports() {
        // Arrange
        when(reportRepository.save(any(Report.class)))
                .thenAnswer(invocation -> {
                    Report report = invocation.getArgument(0);
                    report.setId(System.currentTimeMillis());
                    return report;
                });

        // Act
        Long id1 = reportService.createReport();
        Long id2 = reportService.createReport();
        Long id3 = reportService.createReport();

        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotNull(id3);

        // Проверяем, что save был вызван 3 раза
        verify(reportRepository, times(3)).save(any(Report.class));
    }
}
package ru.Artem.Vinyl.entity;

import jakarta.persistence.*;

/**
 * Сущность отчёта.
 * Хранит информацию о статусе формирования отчёта и его содержимое.
 */
@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Статус формирования отчёта.
     * Возможные значения: CREATED, COMPLETED, ERROR
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    /**
     * Содержимое отчёта в формате HTML.
     * Может быть большим, поэтому используем TEXT тип.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Время создания отчёта (для удобства).
     */
    @Column(name = "created_at")
    private Long createdAt;

    // Конструкторы

    public Report() {
        this.status = ReportStatus.CREATED;
        this.createdAt = System.currentTimeMillis();
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
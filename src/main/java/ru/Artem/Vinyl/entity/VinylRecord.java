package ru.Artem.Vinyl.entity;

import jakarta.persistence.*;

/**
 * Сущность VinylRecord (Виниловая пластинка).
 * Связана с Genre (жанр) и Label (лейбл) через ManyToOne.
 */
@Entity
@Table(name = "vinyl_record")
public class VinylRecord {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @ManyToOne
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String title;

    @Column
    private String condition;

    @Column(nullable = false)
    private Float price;

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
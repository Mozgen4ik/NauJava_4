package ru.Artem.Vinyl.entity;

import jakarta.persistence.*;

/**
 * Сущность Artist (Артист/Исполнитель).
 * Содержит информацию об артисте: имя, страна, биография.
 */
@Entity
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String country;

    @Column(length = 1000)
    private String biography;

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
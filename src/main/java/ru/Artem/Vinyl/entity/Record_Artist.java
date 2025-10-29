package ru.Artem.Vinyl.entity;

import jakarta.persistence.*;

/**
 * Связующая сущность Record_Artist для связи ManyToMany между VinylRecord и Artist.
 * Представляет промежуточную таблицу record_artist.
 */
@Entity
@Table(name = "record_artist")
public class Record_Artist {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private VinylRecord record;

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public VinylRecord getRecord() {
        return record;
    }

    public void setRecord(VinylRecord record) {
        this.record = record;
    }
}
package org.hexarch.song.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Sin @GeneratedValue: el catalogo se siembra por migracion con ids fijos.
@Entity
@Table(name = "songs")
public class SongEntity extends PanacheEntityBase {

    @Id
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(nullable = false, columnDefinition = "text")
    public String title;

    @Column(nullable = false, columnDefinition = "text")
    public String artist;

    @Column(name = "audio_url", nullable = false, columnDefinition = "text")
    public String audioUrl;

    @Column(name = "duration_seconds", nullable = false)
    public int durationSeconds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;
}

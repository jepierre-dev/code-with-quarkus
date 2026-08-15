package org.hexarch.level.infrastructure.adapters.out.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "levels")
public class LevelEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    public UUID id;

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "text")
    public String description;

    // Las canciones viven fuera de este esquema: se guarda solo la referencia.
    @Column(name = "song_id", nullable = false)
    public UUID songId;

    // Nula hasta que un moderador califica el nivel: publicado no implica calificado.
    @Column(name = "difficulty_id")
    public UUID difficultyId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "level_status")
    public LevelStatus status = LevelStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @Column(name = "published_at")
    public LocalDateTime publishedAt;

    // Nula hasta que se sube la primera version: la longitud sale del contenido.
    public Short length;

    @Column(name = "current_version")
    public UUID currentVersionId;
}

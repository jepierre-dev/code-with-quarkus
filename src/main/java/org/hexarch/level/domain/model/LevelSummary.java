package org.hexarch.level.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;

/** Fila de listado. No es el agregado: un listado no necesita cargar el nivel entero. */
public record LevelSummary(
    UUID id,
    String name,
    LevelStatus status,
    UUID difficultyId,
    Short length,
    long likes,
    long downloads,
    LocalDateTime publishedAt
) {
}

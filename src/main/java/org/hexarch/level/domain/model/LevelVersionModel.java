package org.hexarch.level.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.level.domain.exceptions.LevelErrors;

/**
 * Metadatos de una version. El blob del nivel no viaja aqui: listar el historial cargaria
 * todos los binarios en memoria. Se pide aparte por el puerto cuando hace falta.
 */
public record LevelVersionModel(
    UUID id,
    UUID levelId,
    int versionNumber,
    UUID createdBy,
    String checksum,
    String changelog,
    LocalDateTime createdAt
) {

    /** Tope del binario de una version. Lo consumen el DTO del borde y el caso de uso. */
    public static final int MAX_DATA_BYTES = 8 * 1024 * 1024;

    public LevelVersionModel {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(levelId, "levelId must not be null");
        Objects.requireNonNull(createdBy, "createdBy must not be null");
        Objects.requireNonNull(checksum, "checksum must not be null");
        if (versionNumber < 1) {
            throw LevelErrors.invalidVersionNumber(versionNumber);
        }
        changelog = changelog == null ? "" : changelog;
    }

    public LevelVersionModel(UUID levelId, int versionNumber, UUID createdBy, String checksum, String changelog) {
        this(UUID.randomUUID(), levelId, versionNumber, createdBy, checksum, changelog, null);
    }
}

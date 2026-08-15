package org.hexarch.level.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.exceptions.LevelErrors;

public record LevelModel(
    UUID id,
    String name,
    String description,
    UUID songId,
    UUID difficultyId,
    LevelStatus status,
    Short length,
    LocalDateTime createdAt,
    LocalDateTime publishedAt,
    UUID currentVersionId
) {

    private static final int NAME_MIN_LENGTH = 3;
    private static final int NAME_MAX_LENGTH = 64;

    public LevelModel {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(songId, "songId must not be null");
        Objects.requireNonNull(status, "status must not be null");

        name = name == null ? "" : name.trim();
        if (name.length() < NAME_MIN_LENGTH || name.length() > NAME_MAX_LENGTH) {
            throw LevelErrors.invalidLevelName(NAME_MIN_LENGTH, NAME_MAX_LENGTH);
        }
        // Sin descripcion y con descripcion vacia son lo mismo para el jugador: un solo estado.
        description = description == null ? "" : description.trim();

        if (status == LevelStatus.PUBLISHED && currentVersionId == null) {
            throw LevelErrors.publishRequiresVersion(id);
        }
        if (status == LevelStatus.PUBLISHED && publishedAt == null) {
            throw LevelErrors.publishRequiresDate(id);
        }
    }

    /** El id se genera en el dominio para que nunca exista un LevelModel a medias. */
    public static LevelModel create(String name, String description, UUID songId) {
        return new LevelModel(UUID.randomUUID(), name, description, songId, null,
                LevelStatus.DRAFT, null, null, null, null);
    }

    // La longitud sale del contenido, asi que solo cambia cuando cambia la version.
    public LevelModel withCurrentVersion(UUID versionId, short length) {
        Objects.requireNonNull(versionId, "versionId must not be null");
        return new LevelModel(id, name, description, songId, difficultyId, status, length,
                createdAt, publishedAt, versionId);
    }

    public LevelModel publish(LocalDateTime publishedAt) {
        if (currentVersionId == null) {
            throw LevelErrors.publishRequiresVersion(id);
        }
        // Republicar no reescribe la fecha: publishedAt es la primera vez que se hizo publico.
        LocalDateTime firstPublishedAt = this.publishedAt != null ? this.publishedAt : publishedAt;
        return new LevelModel(id, name, description, songId, difficultyId, LevelStatus.PUBLISHED, length,
                createdAt, firstPublishedAt, currentVersionId);
    }

    public LevelModel unpublish() {
        return new LevelModel(id, name, description, songId, difficultyId, LevelStatus.UNLISTED, length,
                createdAt, publishedAt, currentVersionId);
    }

    /** Calificar es cosa de moderacion y es independiente de publicar. */
    public LevelModel rate(UUID difficultyId) {
        Objects.requireNonNull(difficultyId, "difficultyId must not be null");
        return new LevelModel(id, name, description, songId, difficultyId, status, length,
                createdAt, publishedAt, currentVersionId);
    }

    public LevelModel rename(String name, String description) {
        return new LevelModel(id, name, description, songId, difficultyId, status, length,
                createdAt, publishedAt, currentVersionId);
    }

    public boolean isRated() {
        return difficultyId != null;
    }

    public boolean isPublic() {
        return status == LevelStatus.PUBLISHED;
    }

    public boolean isPlayable() {
        return currentVersionId != null;
    }
}

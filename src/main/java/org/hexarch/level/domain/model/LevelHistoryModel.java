package org.hexarch.level.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelAction;

/**
 * Entrada del historial de un nivel. Es append-only: no se edita ni se borra.
 * Casi todo es nulable a proposito, porque cada accion rellena solo los campos que le aplican.
 */
public record LevelHistoryModel(
    UUID id,
    UUID levelId,
    UUID actorId,
    LevelAction action,
    UUID targetUserId,
    UUID versionId,
    String metadata,
    LocalDateTime createdAt
) {

    public LevelHistoryModel {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(levelId, "levelId must not be null");
        Objects.requireNonNull(action, "action must not be null");
    }

    /** actorId nulo cuando la accion la ejecuta el sistema y no una persona. */
    public static LevelHistoryModel of(UUID levelId, UUID actorId, LevelAction action) {
        return new LevelHistoryModel(UUID.randomUUID(), levelId, actorId, action, null, null, null, null);
    }

    public static LevelHistoryModel onMember(UUID levelId, UUID actorId, LevelAction action, UUID targetUserId) {
        return new LevelHistoryModel(UUID.randomUUID(), levelId, actorId, action, targetUserId, null, null, null);
    }

    public static LevelHistoryModel onVersion(UUID levelId, UUID actorId, LevelAction action, UUID versionId) {
        return new LevelHistoryModel(UUID.randomUUID(), levelId, actorId, action, null, versionId, null, null);
    }
}

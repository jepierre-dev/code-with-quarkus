package org.hexarch.level.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;

/** Rol de un usuario sobre un nivel concreto. Es el eje de autorizacion por recurso. */
public record LevelMemberModel(
    UUID levelId,
    UUID userId,
    LevelRole role,
    LocalDateTime joinedAt,
    UUID invitedBy
) {

    public LevelMemberModel {
        Objects.requireNonNull(levelId, "levelId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(role, "role must not be null");
    }

    // invitedBy nulo: al OWNER no lo invito nadie, el nivel es suyo desde que lo creo.
    public static LevelMemberModel owner(UUID levelId, UUID userId) {
        return new LevelMemberModel(levelId, userId, LevelRole.OWNER, null, null);
    }

    public LevelMemberModel withRole(LevelRole role) {
        return new LevelMemberModel(levelId, userId, role, joinedAt, invitedBy);
    }
}

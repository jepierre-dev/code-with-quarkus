package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.model.LevelModel;

public record LevelDto(
        UUID id,
        String name,
        String description,
        UUID songId,
        UUID difficultyId,
        LevelStatus status,
        Short length,
        LocalDateTime createdAt,
        LocalDateTime publishedAt,
        UUID currentVersionId) {

    public static LevelDto from(LevelModel level) {
        return new LevelDto(level.id(), level.name(), level.description(), level.songId(), level.difficultyId(),
                level.status(), level.length(), level.createdAt(), level.publishedAt(), level.currentVersionId());
    }
}

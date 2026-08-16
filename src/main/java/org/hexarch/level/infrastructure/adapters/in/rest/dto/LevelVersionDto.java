package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.model.LevelVersionModel;

public record LevelVersionDto(
        UUID id,
        UUID levelId,
        int versionNumber,
        UUID createdBy,
        String checksum,
        String changelog,
        LocalDateTime createdAt) {

    public static LevelVersionDto from(LevelVersionModel version) {
        return new LevelVersionDto(version.id(), version.levelId(), version.versionNumber(), version.createdBy(),
                version.checksum(), version.changelog(), version.createdAt());
    }
}

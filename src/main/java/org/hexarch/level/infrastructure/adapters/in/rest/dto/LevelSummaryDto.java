package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.model.LevelSummary;

public record LevelSummaryDto(
        UUID id,
        String name,
        LevelStatus status,
        UUID difficultyId,
        Short length,
        long likes,
        long downloads,
        LocalDateTime publishedAt) {

    public static LevelSummaryDto from(LevelSummary summary) {
        return new LevelSummaryDto(summary.id(), summary.name(), summary.status(), summary.difficultyId(),
                summary.length(), summary.likes(), summary.downloads(), summary.publishedAt());
    }
}

package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import org.hexarch.level.domain.model.LevelDetail;

public record LevelDetailDto(
        LevelDto level,
        DifficultyDto difficulty,
        LevelStatsDto stats,
        LevelViewerDto viewer) {

    public static LevelDetailDto from(LevelDetail detail) {
        return new LevelDetailDto(
                LevelDto.from(detail.level()),
                DifficultyDto.from(detail.difficulty()),
                LevelStatsDto.from(detail.stats()),
                LevelViewerDto.from(detail.viewer()));
    }
}

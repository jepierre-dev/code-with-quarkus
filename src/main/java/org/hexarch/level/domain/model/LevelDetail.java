package org.hexarch.level.domain.model;

/** Vista completa de un nivel. difficulty es null mientras no este calificado. */
public record LevelDetail(
    LevelModel level,
    DifficultyModel difficulty,
    LevelStatsModel stats,
    LevelViewer viewer
) {
}

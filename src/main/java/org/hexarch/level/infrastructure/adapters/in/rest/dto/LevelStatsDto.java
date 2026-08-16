package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import org.hexarch.level.domain.model.LevelStatsModel;

public record LevelStatsDto(long downloads, long likes, long plays) {

    public static LevelStatsDto from(LevelStatsModel stats) {
        return new LevelStatsDto(stats.downloads(), stats.likes(), stats.plays());
    }
}

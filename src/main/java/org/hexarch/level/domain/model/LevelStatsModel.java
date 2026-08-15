package org.hexarch.level.domain.model;

/** Proyeccion de solo lectura: no forma parte del agregado Level porque no participa en ninguna invariante. */
public record LevelStatsModel(long downloads, long likes, long plays) {

    public static final LevelStatsModel ZERO = new LevelStatsModel(0, 0, 0);
}

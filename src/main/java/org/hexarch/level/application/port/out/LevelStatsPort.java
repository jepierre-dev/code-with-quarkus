package org.hexarch.level.application.port.out;

import java.util.UUID;

import org.hexarch.level.domain.model.LevelStatsModel;

/** Separado de LevelRepositoryPort: incrementar un contador no debe cargar ni reescribir el agregado. */
public interface LevelStatsPort {

    void initialize(UUID levelId);

    LevelStatsModel findByLevelId(UUID levelId);

    void registerPlay(UUID levelId);

    void registerDownload(UUID levelId);

    /** Devuelve false si el usuario ya habia dado like. */
    boolean like(UUID levelId, UUID userId);

    /** Devuelve false si no habia like que quitar. */
    boolean unlike(UUID levelId, UUID userId);

    boolean hasLiked(UUID levelId, UUID userId);
}

package org.hexarch.level.application.port.in;

import java.util.UUID;

import org.hexarch.level.domain.model.LevelStatsModel;
import org.hexarch.shared.domain.security.Caller;

/** Interaccion del jugador con un nivel. Todo exige identidad: sin ella los contadores no valen nada. */
public interface LevelEngagementUseCase {

    LevelStatsModel like(Caller caller, UUID levelId);

    LevelStatsModel unlike(Caller caller, UUID levelId);

    void registerPlay(Caller caller, UUID levelId);
}

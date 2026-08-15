package org.hexarch.level.application.usecase;

import java.util.UUID;

import org.hexarch.level.application.port.in.LevelEngagementUseCase;
import org.hexarch.level.application.port.out.LevelRepositoryPort;
import org.hexarch.level.application.port.out.LevelStatsPort;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelStatsModel;
import org.hexarch.shared.domain.security.Caller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LevelEngagementService implements LevelEngagementUseCase {

    private final LevelRepositoryPort levelRepository;
    private final LevelStatsPort statsPort;
    private final LevelAccessGuard guard;

    public LevelEngagementService(LevelRepositoryPort levelRepository, LevelStatsPort statsPort,
            LevelAccessGuard guard) {
        this.levelRepository = levelRepository;
        this.statsPort = statsPort;
        this.guard = guard;
    }

    @Override
    @Transactional
    public LevelStatsModel like(Caller caller, UUID levelId) {
        UUID userId = requireVisibleLevel(caller, levelId);
        statsPort.like(levelId, userId);
        return statsPort.findByLevelId(levelId);
    }

    @Override
    @Transactional
    public LevelStatsModel unlike(Caller caller, UUID levelId) {
        UUID userId = requireVisibleLevel(caller, levelId);
        statsPort.unlike(levelId, userId);
        return statsPort.findByLevelId(levelId);
    }

    // Exige identidad aunque no la use: sin ella el contador lo infla un for en la consola del navegador.
    @Override
    @Transactional
    public void registerPlay(Caller caller, UUID levelId) {
        requireVisibleLevel(caller, levelId);
        statsPort.registerPlay(levelId);
    }

    private UUID requireVisibleLevel(Caller caller, UUID levelId) {
        UUID userId = caller.requireUserId();
        LevelModel level = levelRepository.findById(levelId)
                .orElseThrow(() -> LevelErrors.levelNotFound(levelId));
        guard.requireVisible(caller, level);
        return userId;
    }
}

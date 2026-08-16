package org.hexarch.level.application.usecase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.in.LevelsUseCase;
import org.hexarch.level.application.port.out.DifficultyRepositoryPort;
import org.hexarch.level.application.port.out.LevelHistoryRepositoryPort;
import org.hexarch.level.application.port.out.LevelMemberRepositoryPort;
import org.hexarch.level.application.port.out.LevelRepositoryPort;
import org.hexarch.level.application.port.out.LevelStatsPort;
import org.hexarch.level.domain.enums.LevelAction;
import org.hexarch.level.domain.enums.LevelPermission;
import org.hexarch.level.domain.enums.LevelStatus;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.DifficultyModel;
import org.hexarch.level.domain.model.LevelDetail;
import org.hexarch.level.domain.model.LevelHistoryModel;
import org.hexarch.level.domain.model.LevelMemberModel;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelSearchCriteria;
import org.hexarch.level.domain.model.LevelStatsModel;
import org.hexarch.level.domain.model.LevelSummary;
import org.hexarch.level.domain.model.LevelViewer;
import org.hexarch.shared.domain.Page;
import org.hexarch.shared.domain.security.AccessErrors;
import org.hexarch.shared.domain.security.Caller;
import org.hexarch.shared.domain.security.PlatformPermission;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LevelsService implements LevelsUseCase {

    private final LevelRepositoryPort levelRepository;
    private final LevelMemberRepositoryPort memberRepository;
    private final LevelHistoryRepositoryPort historyRepository;
    private final DifficultyRepositoryPort difficultyRepository;
    private final LevelStatsPort statsPort;
    private final LevelAccessGuard guard;

    public LevelsService(LevelRepositoryPort levelRepository, LevelMemberRepositoryPort memberRepository,
            LevelHistoryRepositoryPort historyRepository, DifficultyRepositoryPort difficultyRepository,
            LevelStatsPort statsPort, LevelAccessGuard guard) {
        this.levelRepository = levelRepository;
        this.memberRepository = memberRepository;
        this.historyRepository = historyRepository;
        this.difficultyRepository = difficultyRepository;
        this.statsPort = statsPort;
        this.guard = guard;
    }

    @Override
    public List<DifficultyModel> difficulties() {
        return difficultyRepository.findAll();
    }

    @Override
    public Page<LevelSummary> search(Caller caller, LevelSearchCriteria criteria) {
        return levelRepository.search(visibleCriteria(caller, criteria));
    }
    @Override
    public LevelDetail view(Caller caller, UUID levelId) {
        LevelModel level = requireLevel(levelId);
        guard.requireVisible(caller, level);

        DifficultyModel difficulty = level.difficultyId() == null
                ? null
                : difficultyRepository.findById(level.difficultyId()).orElse(null);
        LevelStatsModel stats = statsPort.findByLevelId(levelId);

        return new LevelDetail(level, difficulty, stats, viewerFor(caller, level));
    }

    @Override
    @Transactional
    public LevelModel create(Caller caller, String name, String description, UUID songId) {
        UUID authorId = caller.requireUserId();

        LevelModel created = levelRepository.create(LevelModel.create(name, description, songId));
        memberRepository.save(LevelMemberModel.owner(created.id(), authorId));
        statsPort.initialize(created.id());
        historyRepository.append(LevelHistoryModel.of(created.id(), authorId, LevelAction.CREATED));
        return created;
    }

    @Override
    @Transactional
    public LevelModel rename(Caller caller, UUID levelId, String name, String description) {
        LevelModel level = requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.EDIT);
        return levelRepository.update(level.rename(name, description));
    }

    @Override
    @Transactional
    public LevelModel publish(Caller caller, UUID levelId) {
        LevelModel level = requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.PUBLISH);

        LevelModel published = levelRepository.update(level.publish(LocalDateTime.now()));
        historyRepository.append(LevelHistoryModel.of(levelId, caller.userId(), LevelAction.PUBLISHED));
        return published;
    }

    @Override
    @Transactional
    public LevelModel unpublish(Caller caller, UUID levelId) {
        LevelModel level = requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.PUBLISH);

        LevelModel unpublished = levelRepository.update(level.unpublish());
        historyRepository.append(LevelHistoryModel.of(levelId, caller.userId(), LevelAction.UNPUBLISHED));
        return unpublished;
    }

    // Calificar es moderacion de plataforma: no depende de ser miembro del nivel.
    @Override
    @Transactional
    public LevelModel rate(Caller caller, UUID levelId, UUID difficultyId) {
        if (!caller.has(PlatformPermission.LEVEL_APPROVE)) {
            throw AccessErrors.permissionDenied(PlatformPermission.LEVEL_APPROVE);
        }
        LevelModel level = requireLevel(levelId);
        difficultyRepository.findById(difficultyId)
                .orElseThrow(() -> LevelErrors.difficultyNotFound(difficultyId));

        return levelRepository.update(level.rate(difficultyId));
    }

    @Override
    @Transactional
    public void delete(Caller caller, UUID levelId) {
        LevelModel level = requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.DELETE);
        levelRepository.update(level.markDeleted());
    }

    private LevelModel requireLevel(UUID levelId) {
        return levelRepository.findById(levelId).orElseThrow(() -> LevelErrors.levelNotFound(levelId));
    }

    // Un anonimo solo busca entre publicados; los borradores propios solo los ve su autor.
    private LevelSearchCriteria visibleCriteria(Caller caller, LevelSearchCriteria criteria) {
        boolean canSeeEverything = caller.has(PlatformPermission.LEVEL_APPROVE)
                || caller.is(criteria.authorId());

        return canSeeEverything
                ? criteria
                : new LevelSearchCriteria(criteria.query(), criteria.difficultyId(), criteria.authorId(),
                        LevelStatus.PUBLISHED, criteria.page(), criteria.size());
    }

    private LevelViewer viewerFor(Caller caller, LevelModel level) {
        if (caller.isAnonymous()) {
            return null;
        }
        return new LevelViewer(
                statsPort.hasLiked(level.id(), caller.userId()),
                guard.roleOf(caller, level.id()).orElse(null),
                guard.can(caller, level.id(), LevelPermission.EDIT),
                guard.can(caller, level.id(), LevelPermission.PUBLISH));
    }
}

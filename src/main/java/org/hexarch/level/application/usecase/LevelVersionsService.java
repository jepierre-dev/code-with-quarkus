package org.hexarch.level.application.usecase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.hexarch.level.application.port.in.LevelVersionsUseCase;
import org.hexarch.level.application.port.out.LevelHistoryRepositoryPort;
import org.hexarch.level.application.port.out.LevelRepositoryPort;
import org.hexarch.level.application.port.out.LevelStatsPort;
import org.hexarch.level.application.port.out.LevelVersionRepositoryPort;
import org.hexarch.level.domain.enums.LevelAction;
import org.hexarch.level.domain.enums.LevelPermission;
import org.hexarch.level.domain.exceptions.LevelErrors;
import org.hexarch.level.domain.model.LevelDownload;
import org.hexarch.level.domain.model.LevelHistoryModel;
import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.domain.model.LevelVersionModel;
import org.hexarch.shared.domain.security.Caller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class LevelVersionsService implements LevelVersionsUseCase {

    private final LevelRepositoryPort levelRepository;
    private final LevelVersionRepositoryPort versionRepository;
    private final LevelHistoryRepositoryPort historyRepository;
    private final LevelStatsPort statsPort;
    private final LevelAccessGuard guard;

    public LevelVersionsService(LevelRepositoryPort levelRepository, LevelVersionRepositoryPort versionRepository,
            LevelHistoryRepositoryPort historyRepository, LevelStatsPort statsPort, LevelAccessGuard guard) {
        this.levelRepository = levelRepository;
        this.versionRepository = versionRepository;
        this.historyRepository = historyRepository;
        this.statsPort = statsPort;
        this.guard = guard;
    }

    @Override
    @Transactional
    public LevelVersionModel upload(Caller caller, UUID levelId, byte[] levelData, String changelog, short length) {
        UUID authorId = caller.requireUserId();
        // Con bloqueo: dos subidas simultaneas al mismo nivel se serializan y no repiten version_number.
        LevelModel level = levelRepository.findByIdForUpdate(levelId)
                .orElseThrow(() -> LevelErrors.levelNotFound(levelId));
        guard.require(caller, levelId, LevelPermission.UPLOAD_VERSION);

        if (levelData == null || levelData.length == 0) {
            throw LevelErrors.emptyLevelData();
        }
        // Se reafirma aqui aunque el DTO ya lo valide: otro adaptador no pasaria por esa anotacion.
        if (levelData.length > LevelVersionModel.MAX_DATA_BYTES) {
            throw LevelErrors.levelDataTooLarge(LevelVersionModel.MAX_DATA_BYTES);
        }

        LevelVersionModel version = new LevelVersionModel(levelId, versionRepository.nextVersionNumber(levelId),
                authorId, checksumOf(levelData), changelog);
        LevelVersionModel created = versionRepository.create(version, levelData);

        levelRepository.update(level.withCurrentVersion(created.id(), length));
        historyRepository.append(
                LevelHistoryModel.onVersion(levelId, authorId, LevelAction.VERSION_CREATED, created.id()));
        return created;
    }

    // El historial de versiones es material de trabajo del equipo, no del publico.
    @Override
    public List<LevelVersionModel> history(Caller caller, UUID levelId) {
        requireLevel(levelId);
        guard.require(caller, levelId, LevelPermission.VIEW_DRAFT);
        return versionRepository.findByLevelId(levelId);
    }

    @Override
    @Transactional
    public LevelDownload download(Caller caller, UUID levelId) {
        caller.requireUserId();
        LevelModel level = requireLevel(levelId);
        guard.requireVisible(caller, level);

        if (level.currentVersionId() == null) {
            throw LevelErrors.versionNotFound(levelId);
        }
        LevelVersionModel version = versionRepository.findById(level.currentVersionId())
                .orElseThrow(() -> LevelErrors.versionNotFound(level.currentVersionId()));
        byte[] data = versionRepository.findDataById(version.id())
                .orElseThrow(() -> LevelErrors.versionNotFound(version.id()));

        statsPort.registerDownload(levelId);
        return new LevelDownload(data, version.checksum(), version.versionNumber());
    }

    private LevelModel requireLevel(UUID levelId) {
        return levelRepository.findById(levelId).orElseThrow(() -> LevelErrors.levelNotFound(levelId));
    }

    // El checksum lo calcula el servidor: si lo mandara el cliente podria mentir sobre el contenido.
    private static String checksumOf(byte[] levelData) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(levelData));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 must be available", e);
        }
    }
}

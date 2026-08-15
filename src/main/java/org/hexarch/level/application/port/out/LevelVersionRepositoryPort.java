package org.hexarch.level.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hexarch.level.domain.model.LevelVersionModel;

public interface LevelVersionRepositoryPort {

    /** El binario viaja aparte del modelo para que listar el historial no cargue todos los blobs. */
    LevelVersionModel create(LevelVersionModel version, byte[] levelData);

    Optional<LevelVersionModel> findById(UUID versionId);

    List<LevelVersionModel> findByLevelId(UUID levelId);

    Optional<byte[]> findDataById(UUID versionId);

    /** Puede competir con otra subida simultanea; la unica (level_id, version_number) es la que decide. */
    int nextVersionNumber(UUID levelId);
}

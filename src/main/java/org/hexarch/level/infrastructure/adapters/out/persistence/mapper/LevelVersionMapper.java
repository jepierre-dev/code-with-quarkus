package org.hexarch.level.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.level.domain.model.LevelVersionModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelVersionEntity;

public final class LevelVersionMapper {

    private LevelVersionMapper() {
    }

    public static LevelVersionEntity toEntity(LevelVersionModel version, byte[] levelData) {
        LevelVersionEntity entity = new LevelVersionEntity();
        entity.id = version.id();
        entity.levelId = version.levelId();
        entity.versionNumber = version.versionNumber();
        entity.createdBy = version.createdBy();
        entity.checksum = version.checksum();
        entity.changelog = version.changelog();
        entity.levelData = levelData;
        return entity;
    }

    /** Deja fuera levelData a proposito: el modelo solo lleva metadatos. */
    public static LevelVersionModel toDomain(LevelVersionEntity entity) {
        return new LevelVersionModel(entity.id, entity.levelId, entity.versionNumber, entity.createdBy,
                entity.checksum, entity.changelog, entity.createdAt);
    }
}

package org.hexarch.level.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.level.domain.model.LevelHistoryModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelHistoryEntity;

public final class LevelHistoryMapper {

    private LevelHistoryMapper() {
    }

    public static LevelHistoryEntity toEntity(LevelHistoryModel entry) {
        LevelHistoryEntity entity = new LevelHistoryEntity();
        entity.id = entry.id();
        entity.levelId = entry.levelId();
        entity.actorId = entry.actorId();
        entity.action = entry.action();
        entity.targetUserId = entry.targetUserId();
        entity.versionId = entry.versionId();
        entity.metadata = entry.metadata();
        return entity;
    }

    public static LevelHistoryModel toDomain(LevelHistoryEntity entity) {
        return new LevelHistoryModel(entity.id, entity.levelId, entity.actorId, entity.action,
                entity.targetUserId, entity.versionId, entity.metadata, entity.createdAt);
    }
}

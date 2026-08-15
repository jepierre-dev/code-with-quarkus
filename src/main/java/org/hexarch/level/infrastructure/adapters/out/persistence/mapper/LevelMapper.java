package org.hexarch.level.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.level.domain.model.LevelModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.LevelEntity;

public final class LevelMapper {

    private LevelMapper() {
    }

    public static LevelEntity toEntity(LevelModel level) {
        LevelEntity entity = new LevelEntity();
        entity.id = level.id();
        copyInto(entity, level);
        return entity;
    }

    /** Vuelca sobre una instancia gestionada: el id y createdAt no se tocan. */
    public static void copyInto(LevelEntity entity, LevelModel level) {
        entity.name = level.name();
        entity.description = level.description();
        entity.songId = level.songId();
        entity.difficultyId = level.difficultyId();
        entity.status = level.status();
        entity.length = level.length();
        entity.publishedAt = level.publishedAt();
        entity.currentVersionId = level.currentVersionId();
    }

    public static LevelModel toDomain(LevelEntity entity) {
        return new LevelModel(entity.id, entity.name, entity.description, entity.songId, entity.difficultyId,
                entity.status, entity.length, entity.createdAt, entity.publishedAt, entity.currentVersionId);
    }
}

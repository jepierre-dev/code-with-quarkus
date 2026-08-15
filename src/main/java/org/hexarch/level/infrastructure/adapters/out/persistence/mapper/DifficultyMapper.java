package org.hexarch.level.infrastructure.adapters.out.persistence.mapper;

import org.hexarch.level.domain.model.DifficultyModel;
import org.hexarch.level.infrastructure.adapters.out.persistence.entity.DifficultyEntity;

public final class DifficultyMapper {

    private DifficultyMapper() {
    }

    public static DifficultyModel toDomain(DifficultyEntity entity) {
        return new DifficultyModel(entity.id, entity.name, entity.stars, entity.icon);
    }
}

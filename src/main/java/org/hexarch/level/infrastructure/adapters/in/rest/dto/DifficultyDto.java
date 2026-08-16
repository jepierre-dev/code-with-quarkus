package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import org.hexarch.level.domain.model.DifficultyModel;

public record DifficultyDto(UUID id, String name, long stars, String icon) {

    public static DifficultyDto from(DifficultyModel difficulty) {
        return difficulty == null
                ? null
                : new DifficultyDto(difficulty.id(), difficulty.name(), difficulty.stars(), difficulty.icon());
    }
}

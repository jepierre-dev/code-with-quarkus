package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record RateLevelDto(@NotNull UUID difficultyId) {
}

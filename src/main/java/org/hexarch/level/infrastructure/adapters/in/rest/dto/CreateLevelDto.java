package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLevelDto(
        @NotBlank @Size(min = 3, max = 64) String name,
        @Size(max = 2000) String description,
        @NotNull UUID songId) {
}

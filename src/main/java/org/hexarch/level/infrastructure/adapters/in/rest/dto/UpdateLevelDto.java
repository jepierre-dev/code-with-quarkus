package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateLevelDto(
        @NotBlank @Size(min = 3, max = 64) String name,
        @Size(max = 2000) String description) {
}

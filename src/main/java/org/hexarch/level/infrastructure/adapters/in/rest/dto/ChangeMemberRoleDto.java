package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import org.hexarch.level.domain.enums.LevelRole;

import jakarta.validation.constraints.NotNull;

public record ChangeMemberRoleDto(@NotNull LevelRole role) {
}

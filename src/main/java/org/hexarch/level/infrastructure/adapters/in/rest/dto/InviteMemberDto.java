package org.hexarch.level.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import org.hexarch.level.domain.enums.LevelRole;

import jakarta.validation.constraints.NotNull;

public record InviteMemberDto(@NotNull UUID userId, @NotNull LevelRole role) {
}

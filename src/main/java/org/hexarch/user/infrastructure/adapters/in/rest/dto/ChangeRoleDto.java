package org.hexarch.user.infrastructure.adapters.in.rest.dto;

import org.hexarch.shared.domain.security.PlatformRole;

import jakarta.validation.constraints.NotNull;

public record ChangeRoleDto(@NotNull PlatformRole role) {
}

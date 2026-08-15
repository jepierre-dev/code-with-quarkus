package org.hexarch.auth.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
        @NotBlank String currentPassword,
        @NotBlank String newPassword) {
}

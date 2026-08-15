package org.hexarch.auth.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank @Size(min = 3, max = 32) String username,
        @NotBlank @Email String email,
        @NotBlank String password) {
}

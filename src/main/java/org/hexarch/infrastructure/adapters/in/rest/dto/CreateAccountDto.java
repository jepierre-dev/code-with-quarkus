package org.hexarch.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(
    @NotBlank(message = "Holder name must not be blank") @NotNull String holderName,
    @NotNull(message = "Initial balance must not be null") @NotNull BigDecimal initialBalance,
    @Nullable @Email(message = "Email must be valid") String email
) {
    
}

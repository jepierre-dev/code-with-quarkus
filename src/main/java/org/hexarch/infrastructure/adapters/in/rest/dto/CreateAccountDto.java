package org.hexarch.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

import io.smallrye.common.constraint.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateAccountDto(
    @NotBlank(message = "Holder name must not be blank") @NotNull String holderName,
    @NotNull(message = "Initial balance must not be null") 
    @PositiveOrZero(message = "Initial balance must be zero or positive") 
        BigDecimal initialBalance,
    @Nullable @Email(message = "Email must be valid") String email
) {
    
}

package org.hexarch.account.infrastructure.adapters.in.rest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountDto(
    @NotBlank(message = "Holder name must not be blank") String holderName,
    @NotNull(message = "Initial balance must not be null") BigDecimal initialBalance,
    @Email(message = "Email must be valid") String email
) {

}

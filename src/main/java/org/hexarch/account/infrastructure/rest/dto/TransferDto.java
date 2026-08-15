package org.hexarch.account.infrastructure.rest.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record TransferDto(
    @NotNull(message = "From account ID must not be null") UUID fromAccountId,
    @NotNull(message = "To account ID must not be null") UUID toAccountId,
    @NotNull(message = "Amount must not be null") BigDecimal amount
) {

}

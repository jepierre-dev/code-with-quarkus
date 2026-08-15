package org.hexarch.infrastructure.adapters.in.rest.dto;

import java.util.UUID;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

public record TransferDto(
    @NotNull(message = "From account ID must not be null") UUID fromAccountId,
    @NotNull(message = "To account ID must not be null") UUID toAccountId,
    @NotNull(message = "Amount must not be null") BigDecimal  amount
) {
    
}

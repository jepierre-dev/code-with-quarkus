package org.hexarch.infrastructure.adapters.in.rest.dto;

import java.util.UUID;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

public record TransferDto(
    @NotNull UUID fromAccountId,
    @NotNull UUID toAccountId,
    @NotNull BigDecimal  amount
) {
    
}

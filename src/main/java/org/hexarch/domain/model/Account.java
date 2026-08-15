package org.hexarch.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.domain.exceptions.BusinessException;

public record Account(UUID id, String holderName, BigDecimal balance) {

    public Account(String holderName, BigDecimal balance) {
        this(null, holderName, balance);
    }

    public Account withDraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("amount must be greater than zero");
        }
        if (balance.compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance");
        }
        return new Account(id, holderName, balance.subtract(amount));
    }

    public Account deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("amount must be greater than zero");
        }
        return new Account(id, holderName, balance.add(amount));
    }

    public Account{
        Objects.requireNonNull(holderName, "holderName must not be null");
        Objects.requireNonNull(balance, "balance must not be null");
    }

}


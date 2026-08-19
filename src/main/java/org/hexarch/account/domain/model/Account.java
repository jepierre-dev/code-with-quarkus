package org.hexarch.account.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import org.hexarch.account.domain.exceptions.AccountErrors;

public record Account(UUID id, String holderName, BigDecimal balance, String email) {

    public Account(String holderName, BigDecimal balance, String email) {
        this(null, holderName, balance, email);
    }

    public Account withDraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw AccountErrors.invalidAmount(amount);
        }
        if (balance.compareTo(amount) < 0) {
            throw AccountErrors.insufficientBalance(balance, amount);
        }
        return new Account(id, holderName, balance.subtract(amount), email);
    }

    public Account deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw AccountErrors.invalidAmount(amount);
        }
        return new Account(id, holderName, balance.add(amount), email);
    }

    // No se puede crear un Account con holderName o balance nulos, y si email no es nulo, debe tener un formato válido
    public Account {
        Objects.requireNonNull(holderName, "holderName must not be null");
        Objects.requireNonNull(balance, "balance must not be null");
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw AccountErrors.invalidEmailFormat(email);
        }
    }
}

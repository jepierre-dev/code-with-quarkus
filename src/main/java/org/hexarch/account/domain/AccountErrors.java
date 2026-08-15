package org.hexarch.account.domain;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.hexarch.shared.domain.DomainException;

/** Catalogo de errores del agregado Account: aporta codigo y datos; el texto vive en errors*.properties. */
public final class AccountErrors {

    private AccountErrors() {
    }

    public static DomainException accountNotFound(UUID accountId) {
        return new DomainException.NotFound(AccountErrorCode.ACCOUNT_NOT_FOUND,
                Map.of("accountId", accountId));
    }

    public static DomainException holderNameAlreadyExists(String holderName) {
        return new DomainException.Conflict(AccountErrorCode.HOLDER_NAME_ALREADY_EXISTS,
                Map.of("holderName", holderName));
    }

    public static DomainException emailAlreadyExists(String email) {
        return new DomainException.Conflict(AccountErrorCode.EMAIL_ALREADY_EXISTS,
                Map.of("email", email));
    }

    public static DomainException invalidAmount(BigDecimal amount) {
        return new DomainException.RuleViolation(AccountErrorCode.INVALID_AMOUNT,
                Map.of("amount", amount));
    }

    public static DomainException insufficientBalance(BigDecimal balance, BigDecimal requested) {
        return new DomainException.RuleViolation(AccountErrorCode.INSUFFICIENT_BALANCE,
                Map.of("balance", balance, "requested", requested));
    }

    public static DomainException invalidEmailFormat(String email) {
        return new DomainException.RuleViolation(AccountErrorCode.INVALID_EMAIL_FORMAT,
                Map.of("email", email));
    }
}

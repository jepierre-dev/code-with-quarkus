package org.hexarch.auth.domain.exceptions;

import java.util.Map;
import java.util.UUID;

import org.hexarch.shared.domain.DomainException;

public final class AuthErrors {

    private AuthErrors() {
    }

    // Sin detalles: distinguir entre email inexistente y contrasena erronea permitiria enumerar cuentas.
    public static DomainException invalidCredentials() {
        return new DomainException.RuleViolation(AuthErrorCode.INVALID_CREDENTIALS);
    }

    public static DomainException accountBanned() {
        return new DomainException.Conflict(AuthErrorCode.ACCOUNT_BANNED);
    }

    public static DomainException weakPassword(int minLength, int maxLength) {
        return new DomainException.RuleViolation(AuthErrorCode.WEAK_PASSWORD,
                Map.of("minLength", minLength, "maxLength", maxLength));
    }

    public static DomainException credentialNotFound(UUID userId) {
        return new DomainException.NotFound(AuthErrorCode.CREDENTIAL_NOT_FOUND, Map.of("userId", userId));
    }
}

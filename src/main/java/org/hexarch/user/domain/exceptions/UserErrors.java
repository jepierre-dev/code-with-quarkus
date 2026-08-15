package org.hexarch.user.domain.exceptions;

import java.util.Map;
import java.util.UUID;

import org.hexarch.shared.domain.DomainException;

public final class UserErrors {

    private UserErrors() {
        // Private constructor to prevent instantiation
    }

    public static DomainException invalidEmail(String email) {
        return new DomainException.RuleViolation(UserErrorCode.INVALID_EMAIL_FORMAT, Map.of("email", email));
    }

    public static DomainException userAlreadyExists(String username, String email) {
        return new DomainException.RuleViolation(UserErrorCode.USER_ALREADY_EXISTS, Map.of("username", username, "email", email));
    }

    public static DomainException userNotFound(UUID userId) {
        return new DomainException.NotFound(UserErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
    }
}

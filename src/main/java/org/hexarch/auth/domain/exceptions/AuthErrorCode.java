package org.hexarch.auth.domain.exceptions;

import org.hexarch.shared.domain.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS("AUTH-001"),
    ACCOUNT_BANNED("AUTH-002"),
    WEAK_PASSWORD("AUTH-003"),
    CREDENTIAL_NOT_FOUND("AUTH-004");

    private final String code;

    AuthErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}

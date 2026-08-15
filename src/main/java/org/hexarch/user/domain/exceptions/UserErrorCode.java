package org.hexarch.user.domain.exceptions;

import org.hexarch.shared.domain.ErrorCode;

public enum UserErrorCode implements ErrorCode{

    INVALID_EMAIL_FORMAT("USER-001"),
    USER_ALREADY_EXISTS("USER-002"),
    USER_NOT_FOUND("USER-003");

    private final String code;

    UserErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}

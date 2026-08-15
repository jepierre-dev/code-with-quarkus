package org.hexarch.shared.domain.security;

import org.hexarch.shared.domain.ErrorCode;

public enum AccessErrorCode implements ErrorCode {

    PERMISSION_DENIED("AUTHZ-001"),
    AUTHENTICATION_REQUIRED("AUTHZ-002");

    private final String code;

    AccessErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}

package org.hexarch.account.domain;

import org.hexarch.shared.domain.ErrorCode;

/** Codigos estables del agregado Account: no reasignar valores ya publicados. */
public enum AccountErrorCode implements ErrorCode {

    INVALID_AMOUNT("ACCOUNT-001"),
    INSUFFICIENT_BALANCE("ACCOUNT-002"),
    INVALID_EMAIL_FORMAT("ACCOUNT-003"),
    ACCOUNT_NOT_FOUND("ACCOUNT-004"),
    HOLDER_NAME_ALREADY_EXISTS("ACCOUNT-005"),
    EMAIL_ALREADY_EXISTS("ACCOUNT-006");

    private final String code;

    AccountErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}

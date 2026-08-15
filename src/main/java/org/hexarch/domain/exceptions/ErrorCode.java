package org.hexarch.domain.exceptions;

/** Codigos estables de negocio: son parte del contrato con el cliente, no reasignar valores existentes. */
public enum ErrorCode {

    INVALID_AMOUNT("ACCOUNT-001"),
    INSUFFICIENT_BALANCE("ACCOUNT-002"),
    INVALID_EMAIL_FORMAT("ACCOUNT-003"),
    ACCOUNT_NOT_FOUND("ACCOUNT-004"),
    HOLDER_NAME_ALREADY_EXISTS("ACCOUNT-005"),
    EMAIL_ALREADY_EXISTS("ACCOUNT-006");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}

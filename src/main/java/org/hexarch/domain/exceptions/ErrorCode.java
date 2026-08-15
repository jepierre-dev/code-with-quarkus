package org.hexarch.domain.exceptions;

/** Codigos estables de negocio: son parte del contrato con el cliente, no renombrar a la ligera. */
public enum ErrorCode {
    INVALID_AMOUNT,
    INSUFFICIENT_BALANCE,
    INVALID_EMAIL_FORMAT,
    ACCOUNT_NOT_FOUND,
    HOLDER_NAME_ALREADY_EXISTS,
    EMAIL_ALREADY_EXISTS
}

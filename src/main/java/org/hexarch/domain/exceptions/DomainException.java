package org.hexarch.domain.exceptions;

import java.util.Map;

/** Sellada: el mapper REST cubre todas las variantes de forma exhaustiva y el compilador lo verifica. */
public abstract sealed class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    private DomainException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public Map<String, Object> details() {
        return details;
    }

    /** La peticion incumple una regla de negocio y no seria valida en ningun estado. */
    public static final class RuleViolation extends DomainException {

        public RuleViolation(ErrorCode errorCode, String message) {
            this(errorCode, message, Map.of());
        }

        public RuleViolation(ErrorCode errorCode, String message, Map<String, Object> details) {
            super(errorCode, message, details);
        }
    }

    /** El recurso referenciado no existe. */
    public static final class NotFound extends DomainException {

        public NotFound(ErrorCode errorCode, String message) {
            this(errorCode, message, Map.of());
        }

        public NotFound(ErrorCode errorCode, String message, Map<String, Object> details) {
            super(errorCode, message, details);
        }
    }

    /** La peticion choca con el estado actual del recurso; la misma peticion podria funcionar mas tarde. */
    public static final class Conflict extends DomainException {

        public Conflict(ErrorCode errorCode, String message) {
            this(errorCode, message, Map.of());
        }

        public Conflict(ErrorCode errorCode, String message, Map<String, Object> details) {
            super(errorCode, message, details);
        }
    }
}

package org.hexarch.shared.domain;

import java.util.Map;

/** Sellada: el mapper REST cubre todas las variantes de forma exhaustiva y el compilador lo verifica. */
public abstract sealed class DomainException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    // getMessage() devuelve el codigo: el texto legible se resuelve en el borde REST segun el locale.
    private DomainException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode.code());
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

        public RuleViolation(ErrorCode errorCode) {
            this(errorCode, Map.of());
        }

        public RuleViolation(ErrorCode errorCode, Map<String, Object> details) {
            super(errorCode, details);
        }
    }

    /** El recurso referenciado no existe. */
    public static final class NotFound extends DomainException {

        public NotFound(ErrorCode errorCode) {
            this(errorCode, Map.of());
        }

        public NotFound(ErrorCode errorCode, Map<String, Object> details) {
            super(errorCode, details);
        }
    }

    /** La peticion choca con el estado actual del recurso; la misma peticion podria funcionar mas tarde. */
    public static final class Conflict extends DomainException {

        public Conflict(ErrorCode errorCode) {
            this(errorCode, Map.of());
        }

        public Conflict(ErrorCode errorCode, Map<String, Object> details) {
            super(errorCode, details);
        }
    }

    /** El llamante esta autenticado pero no tiene permiso sobre la operacion. */
    public static final class Forbidden extends DomainException {

        public Forbidden(ErrorCode errorCode) {
            this(errorCode, Map.of());
        }

        public Forbidden(ErrorCode errorCode, Map<String, Object> details) {
            super(errorCode, details);
        }
    }
}

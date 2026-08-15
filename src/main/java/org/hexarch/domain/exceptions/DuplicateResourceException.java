package org.hexarch.domain.exceptions;

import java.util.Map;

public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }

    public DuplicateResourceException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}

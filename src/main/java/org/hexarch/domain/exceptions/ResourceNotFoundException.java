package org.hexarch.domain.exceptions;

import java.util.Map;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }

    public ResourceNotFoundException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}

package org.hexarch.domain.exceptions;

import java.util.Map;

public class BusinessException extends DomainException {

    public BusinessException(ErrorCode errorCode, String message) {
        super(errorCode, message, Map.of());
    }

    public BusinessException(ErrorCode errorCode, String message, Map<String, Object> details) {
        super(errorCode, message, details);
    }
}

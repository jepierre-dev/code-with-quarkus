package org.hexarch.level.domain.exceptions;

import org.hexarch.shared.domain.ErrorCode;

public enum LevelErrorCode implements ErrorCode {

    LEVEL_NOT_FOUND("LEVEL-001"),
    INVALID_LEVEL_NAME("LEVEL-002"),
    DIFFICULTY_NOT_FOUND("LEVEL-003"),
    VERSION_NOT_FOUND("LEVEL-004"),
    INVALID_VERSION_NUMBER("LEVEL-005"),
    MEMBER_NOT_FOUND("LEVEL-006"),
    PUBLISH_REQUIRES_VERSION("LEVEL-007"),
    PUBLISH_REQUIRES_DATE("LEVEL-008");

    private final String code;

    LevelErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }
}

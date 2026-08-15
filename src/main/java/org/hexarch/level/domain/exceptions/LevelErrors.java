package org.hexarch.level.domain.exceptions;

import java.util.Map;
import java.util.UUID;

import org.hexarch.shared.domain.DomainException;

public final class LevelErrors {

    private LevelErrors() {
    }

    // 404 tambien para borradores ajenos: un 403 confirmaria que ese id existe.
    public static DomainException levelNotFound(UUID levelId) {
        return new DomainException.NotFound(LevelErrorCode.LEVEL_NOT_FOUND, Map.of("levelId", levelId));
    }

    public static DomainException invalidLevelName(int minLength, int maxLength) {
        return new DomainException.RuleViolation(LevelErrorCode.INVALID_LEVEL_NAME,
                Map.of("minLength", minLength, "maxLength", maxLength));
    }

    public static DomainException difficultyNotFound(UUID difficultyId) {
        return new DomainException.NotFound(LevelErrorCode.DIFFICULTY_NOT_FOUND,
                Map.of("difficultyId", difficultyId));
    }

    public static DomainException versionNotFound(UUID versionId) {
        return new DomainException.NotFound(LevelErrorCode.VERSION_NOT_FOUND, Map.of("versionId", versionId));
    }

    public static DomainException invalidVersionNumber(int versionNumber) {
        return new DomainException.RuleViolation(LevelErrorCode.INVALID_VERSION_NUMBER,
                Map.of("versionNumber", versionNumber));
    }

    public static DomainException memberNotFound(UUID levelId, UUID userId) {
        return new DomainException.NotFound(LevelErrorCode.MEMBER_NOT_FOUND,
                Map.of("levelId", levelId, "userId", userId));
    }

    public static DomainException publishRequiresVersion(UUID levelId) {
        return new DomainException.RuleViolation(LevelErrorCode.PUBLISH_REQUIRES_VERSION,
                Map.of("levelId", levelId));
    }

    public static DomainException publishRequiresDate(UUID levelId) {
        return new DomainException.RuleViolation(LevelErrorCode.PUBLISH_REQUIRES_DATE,
                Map.of("levelId", levelId));
    }
}

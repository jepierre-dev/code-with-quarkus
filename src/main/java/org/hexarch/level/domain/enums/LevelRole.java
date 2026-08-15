package org.hexarch.level.domain.enums;

import java.util.EnumSet;
import java.util.Set;

/** Refleja el tipo enum level_role de PostgreSQL. El mapeo a permisos vive en codigo, como PlatformRole. */
public enum LevelRole {

    OWNER(EnumSet.allOf(LevelPermission.class)),

    BUILDER(EnumSet.of(
            LevelPermission.VIEW_DRAFT,
            LevelPermission.EDIT,
            LevelPermission.UPLOAD_VERSION)),

    DECORATOR(EnumSet.of(
            LevelPermission.VIEW_DRAFT,
            LevelPermission.UPLOAD_VERSION)),

    VERIFIER(EnumSet.of(LevelPermission.VIEW_DRAFT)),

    VIEWER(EnumSet.of(LevelPermission.VIEW_DRAFT));

    private final Set<LevelPermission> permissions;

    LevelRole(Set<LevelPermission> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public Set<LevelPermission> permissions() {
        return permissions;
    }

    public boolean has(LevelPermission permission) {
        return permissions.contains(permission);
    }
}

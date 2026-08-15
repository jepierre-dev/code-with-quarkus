package org.hexarch.shared.domain.security;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/** Rol global del usuario. El mapeo a permisos vive aqui: cambiarlo es un cambio de codigo, no de datos. */
public enum PlatformRole {

    PLAYER(EnumSet.noneOf(PlatformPermission.class)),

    MODERATOR(EnumSet.of(
            PlatformPermission.USER_BAN,
            PlatformPermission.LEVEL_APPROVE,
            PlatformPermission.LEVEL_FEATURE)),

    ADMIN(EnumSet.allOf(PlatformPermission.class));

    private final Set<PlatformPermission> permissions;

    PlatformRole(Set<PlatformPermission> permissions) {
        this.permissions = Set.copyOf(permissions);
    }

    public Set<PlatformPermission> permissions() {
        return permissions;
    }

    public boolean has(PlatformPermission permission) {
        return permissions.contains(permission);
    }

    /** Un token con un rol desconocido o sin rol se trata como el minimo privilegio. */
    public static PlatformRole fromGroups(Collection<String> groups) {
        if (groups == null) {
            return PLAYER;
        }
        for (PlatformRole role : values()) {
            if (groups.contains(role.name())) {
                return role;
            }
        }
        return PLAYER;
    }
}

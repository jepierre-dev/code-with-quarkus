package org.hexarch.shared.domain.security;

import java.util.UUID;

/**
 * Quien hace la peticion. Nunca es null: un visitante anonimo es {@link #ANONYMOUS}.
 * Existe para que los casos de uso no dependan de JsonWebToken ni de nada de HTTP.
 */
public record Caller(UUID userId, PlatformRole role) {

    public static final Caller ANONYMOUS = new Caller(null, PlatformRole.PLAYER);

    public boolean isAnonymous() {
        return userId == null;
    }

    public boolean has(PlatformPermission permission) {
        return role.has(permission);
    }

    public boolean is(UUID otherUserId) {
        return userId != null && userId.equals(otherUserId);
    }

    public UUID requireUserId() {
        if (isAnonymous()) {
            throw AccessErrors.authenticationRequired();
        }
        return userId;
    }
}

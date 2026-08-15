package org.hexarch.shared.domain.security;

import java.util.Map;

import org.hexarch.shared.domain.DomainException;

public final class AccessErrors {

    private AccessErrors() {
    }

    public static DomainException permissionDenied(PlatformPermission permission) {
        return new DomainException.Forbidden(AccessErrorCode.PERMISSION_DENIED,
                Map.of("permission", permission.name()));
    }

    // La lanza el dominio cuando una operacion necesita identidad; el borde REST ya deberia haberla exigido.
    public static DomainException authenticationRequired() {
        return new DomainException.Forbidden(AccessErrorCode.AUTHENTICATION_REQUIRED);
    }
}

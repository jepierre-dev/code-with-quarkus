package org.hexarch.shared.infrastructure.security;

import java.util.UUID;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hexarch.shared.domain.security.Caller;
import org.hexarch.shared.domain.security.PlatformRole;

import io.quarkus.security.UnauthorizedException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Traduce el token HTTP al tipo que entienden los casos de uso.
 * Es un resolutor y no un productor porque Caller es un record: CDI no puede proxiar clases finales.
 */
@ApplicationScoped
public class CallerResolver {

    @Inject
    JsonWebToken jwt;

    /** Unico punto del proyecto donde se pregunta si la peticion trae token. */
    public Caller current() {
        if (jwt.getRawToken() == null) {
            return Caller.ANONYMOUS;
        }
        return new Caller(subjectOf(jwt), PlatformRole.fromGroups(jwt.getGroups()));
    }

    // Un token bien firmado pero con un sub que no es UUID no es nuestro: 401, no 500.
    private static UUID subjectOf(JsonWebToken jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new UnauthorizedException();
        }
    }
}

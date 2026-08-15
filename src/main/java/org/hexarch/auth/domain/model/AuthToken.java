package org.hexarch.auth.domain.model;

/** Credencial emitida tras un login correcto. */
public record AuthToken(String accessToken, String tokenType, long expiresInSeconds) {

    public static AuthToken bearer(String accessToken, long expiresInSeconds) {
        return new AuthToken(accessToken, "Bearer", expiresInSeconds);
    }
}

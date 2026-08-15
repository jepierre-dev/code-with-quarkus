package org.hexarch.auth.infrastructure.adapters.in.rest.dto;

import org.hexarch.auth.domain.model.AuthToken;

public record AuthTokenDto(String accessToken, String tokenType, long expiresInSeconds) {

    public static AuthTokenDto from(AuthToken token) {
        return new AuthTokenDto(token.accessToken(), token.tokenType(), token.expiresInSeconds());
    }
}

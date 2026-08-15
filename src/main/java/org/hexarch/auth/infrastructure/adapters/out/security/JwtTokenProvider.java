package org.hexarch.auth.infrastructure.adapters.out.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hexarch.auth.application.port.out.TokenProviderPort;
import org.hexarch.auth.domain.model.AuthToken;

import io.smallrye.jwt.build.Jwt;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtTokenProvider implements TokenProviderPort {

    private final String issuer;
    private final long expirationSeconds;

    public JwtTokenProvider(
            @ConfigProperty(name = "hexarch.jwt.issuer") String issuer,
            @ConfigProperty(name = "hexarch.jwt.expiration-seconds") long expirationSeconds) {
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    // El subject es el id del usuario: el email puede cambiar y no sirve como identidad estable.
    @Override
    public AuthToken issue(UUID userId, String email) {
        String token = Jwt.issuer(issuer)
                .subject(userId.toString())
                .upn(email)
                .groups(Set.of("user"))
                .expiresAt(Instant.now().plusSeconds(expirationSeconds))
                .sign();

        return AuthToken.bearer(token, expirationSeconds);
    }
}

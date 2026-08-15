package org.hexarch.auth.application.port.out;

import java.util.UUID;

import org.hexarch.auth.domain.model.AuthToken;
import org.hexarch.shared.domain.security.PlatformRole;

/** Deja el formato y la firma del token fuera del dominio. */
public interface TokenProviderPort {

    AuthToken issue(UUID userId, String email, PlatformRole role);
}

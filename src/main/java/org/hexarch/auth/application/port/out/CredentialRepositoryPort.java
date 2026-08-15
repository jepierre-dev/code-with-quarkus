package org.hexarch.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface CredentialRepositoryPort {

    void save(UUID userId, String passHash);

    void updateHash(UUID userId, String passHash);

    Optional<String> findHashByUserId(UUID userId);
}

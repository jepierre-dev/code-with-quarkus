package org.hexarch.auth.infrastructure.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.auth.application.port.out.CredentialRepositoryPort;
import org.hexarch.auth.domain.exceptions.AuthErrors;
import org.hexarch.auth.infrastructure.adapters.out.persistence.entity.CredentialEntity;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CredentialRepositoryAdapter implements CredentialRepositoryPort {

    @Override
    public void save(UUID userId, String passHash) {
        CredentialEntity entity = new CredentialEntity();
        entity.userId = userId;
        entity.passHash = passHash;
        entity.persist();
    }

    // Muta la instancia gestionada: Hibernate emite el UPDATE por dirty checking al hacer flush.
    @Override
    public void updateHash(UUID userId, String passHash) {
        CredentialEntity managed = CredentialEntity.findById(userId);
        if (managed == null) {
            throw AuthErrors.credentialNotFound(userId);
        }
        managed.passHash = passHash;
    }

    @Override
    public Optional<String> findHashByUserId(UUID userId) {
        CredentialEntity entity = CredentialEntity.findById(userId);
        return entity == null ? Optional.empty() : Optional.of(entity.passHash);
    }
}

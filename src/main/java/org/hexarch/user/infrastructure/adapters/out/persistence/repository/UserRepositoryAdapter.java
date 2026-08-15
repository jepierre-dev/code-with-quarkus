package org.hexarch.user.infrastructure.adapters.out.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformRole;
import org.hexarch.user.application.port.out.UserRepositoryPort;
import org.hexarch.user.domain.exceptions.UserErrors;
import org.hexarch.user.domain.model.UserModel;
import org.hexarch.user.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.hexarch.user.infrastructure.adapters.out.persistence.mapper.UserMapper;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepositoryAdapter implements UserRepositoryPort {

    @Override
    public UserModel create(UserModel user) {
        UserEntity entity = UserMapper.toEntity(user);
        entity.persist();
        return UserMapper.toDomain(entity);
    }

    @Override
    public Optional<UserModel> findById(UUID userId) {
        UserEntity entity = UserEntity.findById(userId);
        return entity == null ? Optional.empty() : Optional.of(UserMapper.toDomain(entity));
    }

    @Override
    public Optional<UserModel> findByEmail(String email) {
        return UserEntity.<UserEntity>find("email", email).firstResultOptional().map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsernameOrEmail(String username, String email) {
        return UserEntity.count("name = ?1 or email = ?2", username, email) > 0;
    }

    // Muta la instancia gestionada: Hibernate emite el UPDATE por dirty checking al hacer flush.
    @Override
    public UserModel setBanned(UUID userId, boolean banned) {
        UserEntity managed = requireById(userId);
        managed.banned = banned;
        return UserMapper.toDomain(managed);
    }

    @Override
    public UserModel setRole(UUID userId, PlatformRole role) {
        UserEntity managed = requireById(userId);
        managed.role = role;
        return UserMapper.toDomain(managed);
    }

    private static UserEntity requireById(UUID userId) {
        UserEntity managed = UserEntity.findById(userId);
        if (managed == null) {
            throw UserErrors.userNotFound(userId);
        }
        return managed;
    }
}

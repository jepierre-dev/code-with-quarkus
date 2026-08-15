package org.hexarch.user.application.port.out;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformRole;
import org.hexarch.user.domain.model.UserModel;

public interface UserRepositoryPort {

    UserModel create(UserModel user);

    Optional<UserModel> findById(UUID userId);

    Optional<UserModel> findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    UserModel setBanned(UUID userId, boolean banned);

    UserModel setRole(UUID userId, PlatformRole role);
}

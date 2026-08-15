package org.hexarch.user.application.port.in;

import java.util.Optional;
import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformRole;
import org.hexarch.user.domain.model.UserModel;

// Sin contrasenas: las credenciales son responsabilidad del contexto auth.
public interface UsersUseCase {

    UserModel createUser(String username, String email);

    UserModel findById(UUID userId);

    Optional<UserModel> findByEmail(String email);

    UserModel banUser(UUID userId);

    UserModel unbanUser(UUID userId);

    UserModel changeRole(UUID userId, PlatformRole role);
}

package org.hexarch.auth.application.port.in;

import java.util.UUID;

import org.hexarch.auth.domain.model.AuthToken;

public interface AuthUseCase {

    AuthToken register(String username, String email, String password);

    AuthToken login(String email, String password);

    void changePassword(UUID userId, String currentPassword, String newPassword);
}

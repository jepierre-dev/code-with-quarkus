package org.hexarch.user.infrastructure.adapters.in.rest.dto;

import java.util.UUID;

import org.hexarch.shared.domain.security.PlatformRole;
import org.hexarch.user.domain.model.UserModel;

public record UserDto(UUID id, String username, String email, boolean banned, PlatformRole role) {

    public static UserDto from(UserModel user) {
        return new UserDto(user.id(), user.username(), user.email(), user.banned(), user.role());
    }
}

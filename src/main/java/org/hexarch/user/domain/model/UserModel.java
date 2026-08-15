package org.hexarch.user.domain.model;

import java.util.Objects;
import java.util.UUID;

import org.hexarch.user.domain.exceptions.UserErrors;

public record UserModel(
    UUID id,
    String username,
    String email,
    boolean banned
) {

    public UserModel(String username, String email) {
        this(null, username, email, false);
    }

    public UserModel{
        Objects.requireNonNull(username, "username must not be null");
        Objects.requireNonNull(email, "email must not be null");
        if(!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw UserErrors.invalidEmail(email);
        }

    }

    public UserModel withBanned(boolean banned) {
        return new UserModel(id, username, email, banned);
    }

}

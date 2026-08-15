package org.hexarch.auth.domain.model;

import java.util.Objects;

import org.hexarch.auth.domain.exceptions.AuthErrors;

/** Contraseña en claro. Solo existe en memoria durante el registro o el cambio de contraseña. */
public record RawPassword(String value) {

    private static final int MIN_LENGTH = 8;
    // Bcrypt trunca por encima de 72 bytes, asi que mas alla no aporta seguridad.
    private static final int MAX_LENGTH = 72;

    public RawPassword {
        Objects.requireNonNull(value, "password must not be null");
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw AuthErrors.weakPassword(MIN_LENGTH, MAX_LENGTH);
        }
        if (!value.matches(".*[A-Za-z].*") || !value.matches(".*\\d.*")) {
            throw AuthErrors.weakPassword(MIN_LENGTH, MAX_LENGTH);
        }
    }

    // Evita que la contraseña acabe en un log por un toString accidental.
    @Override
    public String toString() {
        return "RawPassword[value=***]";
    }
}

package org.hexarch.auth.application.port.out;

import org.hexarch.auth.domain.model.RawPassword;

/** Deja el algoritmo de hashing fuera del dominio. */
public interface PasswordHasherPort {

    String hash(RawPassword rawPassword);

    boolean matches(String rawPassword, String passHash);
}

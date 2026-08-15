package org.hexarch.auth.infrastructure.adapters.out.security;

import org.hexarch.auth.application.port.out.PasswordHasherPort;
import org.hexarch.auth.domain.model.RawPassword;

import io.quarkus.elytron.security.common.BcryptUtil;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BcryptPasswordHasher implements PasswordHasherPort {

    @Override
    public String hash(RawPassword rawPassword) {
        return BcryptUtil.bcryptHash(rawPassword.value());
    }

    @Override
    public boolean matches(String rawPassword, String passHash) {
        return BcryptUtil.matches(rawPassword, passHash);
    }
}

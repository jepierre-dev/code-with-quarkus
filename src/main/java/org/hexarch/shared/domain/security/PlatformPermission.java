package org.hexarch.shared.domain.security;

/** Permisos de plataforma. No dependen del recurso concreto, solo de quien hace la peticion. */
public enum PlatformPermission {

    USER_BAN,
    USER_ROLE_ASSIGN,
    LEVEL_APPROVE,
    LEVEL_FEATURE,
    LEVEL_DELETE_ANY
}

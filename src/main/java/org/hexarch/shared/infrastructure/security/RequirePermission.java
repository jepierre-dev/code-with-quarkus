package org.hexarch.shared.infrastructure.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hexarch.shared.domain.security.PlatformPermission;

/** Exige un permiso de plataforma. Implica autenticacion: sin token la peticion recibe 401. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface RequirePermission {

    PlatformPermission value();
}

package org.hexarch.shared.infrastructure.security;

import java.lang.reflect.Method;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hexarch.shared.domain.security.AccessErrors;
import org.hexarch.shared.domain.security.PlatformRole;

import io.quarkus.security.UnauthorizedException;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

// Primera barrera en el borde HTTP. La regla de negocio se reafirma en el caso de uso.
@Provider
@Priority(Priorities.AUTHORIZATION)
public class RequirePermissionFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        RequirePermission required = requiredPermissionOf();
        if (required == null) {
            return;
        }
        if (jwt.getRawToken() == null) {
            throw new UnauthorizedException();
        }
        if (!PlatformRole.fromGroups(jwt.getGroups()).has(required.value())) {
            throw AccessErrors.permissionDenied(required.value());
        }
    }

    // El metodo gana a la clase: permite marcar un recurso entero y relajar un endpoint concreto.
    private RequirePermission requiredPermissionOf() {
        Method method = resourceInfo.getResourceMethod();
        if (method == null) {
            return null;
        }
        RequirePermission onMethod = method.getAnnotation(RequirePermission.class);
        return onMethod != null ? onMethod : resourceInfo.getResourceClass().getAnnotation(RequirePermission.class);
    }
}

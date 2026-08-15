package org.hexarch.infrastructure.adapters.in.rest.exceptions;

import java.time.Instant;

import org.hexarch.domain.exceptions.DomainException;
import org.hexarch.domain.exceptions.DuplicateResourceException;
import org.hexarch.domain.exceptions.ResourceNotFoundException;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiError;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiResponse;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class DomainExceptionMapper implements ExceptionMapper<DomainException> {

    private static final int UNPROCESSABLE_ENTITY = 422;

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(DomainException exception) {
        int status = httpStatusOf(exception);

        ApiError error = new ApiError(
                exception.errorCode().name(),
                exception.getMessage(),
                exception.details(),
                Instant.now(),
                uriInfo.getPath());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiResponse<Void>(status, null, exception.getMessage(), error))
                .build();
    }

    // Unico punto donde el dominio se traduce a semantica HTTP.
    private static int httpStatusOf(DomainException exception) {
        return switch (exception) {
            case ResourceNotFoundException e -> Response.Status.NOT_FOUND.getStatusCode();
            case DuplicateResourceException e -> Response.Status.CONFLICT.getStatusCode();
            default -> UNPROCESSABLE_ENTITY;
        };
    }
}

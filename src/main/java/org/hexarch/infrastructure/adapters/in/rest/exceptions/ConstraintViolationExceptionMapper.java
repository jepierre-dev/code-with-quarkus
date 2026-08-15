package org.hexarch.infrastructure.adapters.in.rest.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hexarch.infrastructure.adapters.in.rest.response.ApiError;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Sustituye al mapper built-in de Quarkus para unificar el formato de error.
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    private static final String MESSAGE = "Request validation failed";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            details.merge(fieldOf(violation), violation.getMessage(),
                    (existing, added) -> existing + "; " + added);
        }

        int status = Response.Status.BAD_REQUEST.getStatusCode();
        ApiError error = new ApiError(VALIDATION_FAILED, MESSAGE, details, Instant.now(), uriInfo.getPath());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiResponse<Void>(status, null, MESSAGE, error))
                .build();
    }

    // El path completo expone nombres de metodo y parametro internos; solo interesa el campo.
    private static String fieldOf(ConstraintViolation<?> violation) {
        String field = null;
        for (Node node : violation.getPropertyPath()) {
            field = node.getName();
        }
        return field == null ? "request" : field;
    }
}

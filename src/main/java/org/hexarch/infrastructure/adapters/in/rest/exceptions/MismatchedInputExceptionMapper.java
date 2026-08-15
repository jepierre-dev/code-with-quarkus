package org.hexarch.infrastructure.adapters.in.rest.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hexarch.infrastructure.adapters.in.rest.response.ApiError;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiResponse;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Sustituye a BuiltinMismatchedInputExceptionMapper para unificar el formato de error.
@Provider
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    private static final String MALFORMED_REQUEST_BODY = "MALFORMED_REQUEST_BODY";
    private static final String MESSAGE = "Request body could not be parsed";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(MismatchedInputException exception) {
        Map<String, Object> details = new LinkedHashMap<>();
        String field = fieldOf(exception);
        if (field != null) {
            details.put(field, "Invalid value for expected type " + typeOf(exception));
        }

        int status = Response.Status.BAD_REQUEST.getStatusCode();
        ApiError error = new ApiError(MALFORMED_REQUEST_BODY, MESSAGE, details, Instant.now(), uriInfo.getPath());

        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ApiResponse<Void>(status, null, MESSAGE, error))
                .build();
    }

    private static String fieldOf(MismatchedInputException exception) {
        String field = null;
        for (Reference reference : exception.getPath()) {
            if (reference.getFieldName() != null) {
                field = reference.getFieldName();
            }
        }
        return field;
    }

    private static String typeOf(MismatchedInputException exception) {
        return exception.getTargetType() == null ? "unknown" : exception.getTargetType().getSimpleName();
    }
}

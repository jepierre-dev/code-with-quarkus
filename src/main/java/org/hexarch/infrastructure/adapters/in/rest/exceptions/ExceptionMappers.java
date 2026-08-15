package org.hexarch.infrastructure.adapters.in.rest.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hexarch.domain.exceptions.DomainException;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiError;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiResponse;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

// Unico punto de traduccion de excepciones a respuestas HTTP.
public class ExceptionMappers {

    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();

    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> domain(DomainException exception, UriInfo uriInfo) {
        return error(httpStatusOf(exception), exception.errorCode().code(), exception.getMessage(),
                exception.details(), uriInfo);
    }

    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> validation(ConstraintViolationException exception, UriInfo uriInfo) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            details.merge(fieldOf(violation), violation.getMessage(),
                    (existing, added) -> existing + "; " + added);
        }
        return error(BAD_REQUEST, "REQUEST-001", "Request validation failed", details, uriInfo);
    }

    // Sustituye a BuiltinMismatchedInputExceptionMapper de quarkus-rest-jackson.
    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> malformedBody(MismatchedInputException exception, UriInfo uriInfo) {
        Map<String, Object> details = new LinkedHashMap<>();
        String field = fieldOf(exception);
        if (field != null) {
            details.put(field, "Invalid value for expected type " + typeOf(exception));
        }
        return error(BAD_REQUEST, "REQUEST-002", "Request body could not be parsed", details, uriInfo);
    }

    private static RestResponse<ApiResponse<Void>> error(int status, String code, String message,
            Map<String, Object> details, UriInfo uriInfo) {
        ApiError apiError = new ApiError(code, message, details, Instant.now(), uriInfo.getPath());

        return RestResponse.ResponseBuilder.<ApiResponse<Void>>create(status)
                .entity(new ApiResponse<>(status, null, message, apiError))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }

    // Switch exhaustivo sobre la jerarquia sellada: una variante nueva rompe la compilacion aqui.
    private static int httpStatusOf(DomainException exception) {
        return switch (exception) {
            case DomainException.NotFound e -> Response.Status.NOT_FOUND.getStatusCode();
            case DomainException.Conflict e -> Response.Status.CONFLICT.getStatusCode();
            case DomainException.RuleViolation e -> UNPROCESSABLE_ENTITY;
        };
    }

    // El path completo expone nombres de metodo y parametro internos; solo interesa el campo.
    private static String fieldOf(ConstraintViolation<?> violation) {
        String field = null;
        for (Node node : violation.getPropertyPath()) {
            field = node.getName();
        }
        return field == null ? "request" : field;
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

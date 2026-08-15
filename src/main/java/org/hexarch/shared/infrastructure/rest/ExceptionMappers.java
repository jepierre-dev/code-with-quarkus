package org.hexarch.shared.infrastructure.rest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.hexarch.shared.domain.DomainException;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

// Unico punto de traduccion de excepciones a respuestas HTTP.
public class ExceptionMappers {

    private static final int UNPROCESSABLE_ENTITY = 422;
    private static final int BAD_REQUEST = Response.Status.BAD_REQUEST.getStatusCode();

    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> domain(DomainException exception, UriInfo uriInfo, HttpHeaders headers) {
        String code = exception.errorCode().code();
        return error(httpStatusOf(exception), code, exception.details(), uriInfo, headers);
    }

    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> validation(ConstraintViolationException exception, UriInfo uriInfo,
            HttpHeaders headers) {
        Map<String, Object> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            details.merge(fieldOf(violation), violation.getMessage(),
                    (existing, added) -> existing + "; " + added);
        }
        return error(BAD_REQUEST, "REQUEST-001", details, uriInfo, headers);
    }

    // Sustituye a BuiltinMismatchedInputExceptionMapper de quarkus-rest-jackson.
    @ServerExceptionMapper
    public RestResponse<ApiResponse<Void>> malformedBody(MismatchedInputException exception, UriInfo uriInfo,
            HttpHeaders headers) {
        Map<String, Object> details = new LinkedHashMap<>();
        String field = fieldOf(exception);
        if (field != null) {
            details.put(field, "Invalid value for expected type " + typeOf(exception));
        }
        return error(BAD_REQUEST, "REQUEST-002", details, uriInfo, headers);
    }

    private static RestResponse<ApiResponse<Void>> error(int status, String code, Map<String, Object> details,
            UriInfo uriInfo, HttpHeaders headers) {
        ResourceBundle bundle = ErrorMessages.bundleFor(headers);
        String message = ErrorMessages.resolve(bundle, code, details);
        ApiError apiError = new ApiError(code, message, details, Instant.now(), uriInfo.getPath());

        return RestResponse.ResponseBuilder.<ApiResponse<Void>>create(status)
                .entity(new ApiResponse<>(status, null, message, apiError))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.CONTENT_LANGUAGE, ErrorMessages.languageTagOf(bundle))
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

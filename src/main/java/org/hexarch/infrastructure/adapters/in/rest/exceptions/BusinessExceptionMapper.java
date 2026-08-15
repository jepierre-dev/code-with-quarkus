package org.hexarch.infrastructure.adapters.in.rest.exceptions;

import org.hexarch.domain.exceptions.BusinessException;
import org.hexarch.infrastructure.adapters.in.rest.response.ApiResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {
        int status = 422;
        ApiResponse<Void> response = new ApiResponse<>(status, null, exception.getMessage());

        return Response.status(status)
            .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .build();
    }
}
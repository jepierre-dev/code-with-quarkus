package org.hexarch.infrastructure.adapters.in.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
    Integer status,
    @JsonInclude(JsonInclude.Include.NON_NULL) T data,
    String message,
    @JsonInclude(JsonInclude.Include.NON_NULL) ApiError error
) {

    public ApiResponse(Integer status, T data, String message) {
        this(status, data, message, null);
    }
}

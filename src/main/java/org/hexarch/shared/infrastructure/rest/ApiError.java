package org.hexarch.shared.infrastructure.rest;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiError(
    String code,
    String message,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, Object> details,
    Instant timestamp,
    String path
) {

}

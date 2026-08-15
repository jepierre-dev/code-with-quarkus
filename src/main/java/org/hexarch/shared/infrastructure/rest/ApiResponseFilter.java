package org.hexarch.shared.infrastructure.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApiWraped
public class ApiResponseFilter implements ContainerResponseFilter {

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        ApiWraped apiWraped = resourceInfo.getResourceMethod().getAnnotation(ApiWraped.class);

        String message = apiWraped.message();
        boolean includeStatus = apiWraped.includeStatus();

        Object body = responseContext.getEntity();

        ApiResponse<Object> apiResponse = new ApiResponse<>(
            includeStatus ? responseContext.getStatus() : null,
            body,
            message
        );
        responseContext.setEntity(apiResponse);
    }
}

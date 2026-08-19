package org.hexarch.shared.infrastructure.rest;

import java.util.Map;
import java.util.ResourceBundle;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApiWraped
public class ApiResponseFilter implements ContainerResponseFilter {

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        // Los errores ya vienen con su forma final desde ExceptionMappers: envolverlos otra vez los anidaria.
        if (responseContext.getStatusInfo().getFamily() != Family.SUCCESSFUL) {
            return;
        }

        ApiWraped apiWraped = resourceInfo.getResourceMethod().getAnnotation(ApiWraped.class);

        boolean includeStatus = apiWraped.includeStatus();
        ResourceBundle bundle = Messages.bundleFor(Messages.MESSAGES, requestContext.getAcceptableLanguages());
        String message = Messages.resolve(bundle, apiWraped.message(), Map.of());

        Object body = responseContext.getEntity();

        ApiResponse<Object> apiResponse = new ApiResponse<>(
            includeStatus ? responseContext.getStatus() : null,
            body,
            message
        );
        responseContext.setEntity(apiResponse);
        responseContext.getHeaders().putSingle(HttpHeaders.CONTENT_LANGUAGE, Messages.languageTagOf(bundle));
    }
}

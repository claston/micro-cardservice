package com.sistema.customer.api.error;

import com.sistema.common.api.error.CommonErrorTypes;
import com.sistema.common.api.error.ErrorResponse;
import com.sistema.common.api.error.ErrorResponseFactory;
import com.sistema.common.api.trace.TraceIdProvider;
import com.sistema.customer.application.exception.CustomerException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CustomerExceptionMapper implements ExceptionMapper<CustomerException> {
    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(CustomerException exception) {
        int status = exception.getStatus();
        String type = mapType(status);
        String title = mapTitle(status);
        ErrorResponse response = ErrorResponseFactory.build(
                type,
                title,
                status,
                exception.getMessage(),
                ErrorResponseFactory.resolveInstance(uriInfo),
                exception.getErrorCode(),
                null,
                traceIdProvider
        );
        return Response.status(status).entity(response).build();
    }

    private String mapType(int status) {
        return switch (status) {
            case 401 -> CommonErrorTypes.UNAUTHORIZED;
            case 404 -> CommonErrorTypes.NOT_FOUND;
            case 409 -> CommonErrorTypes.CONFLICT;
            default -> CommonErrorTypes.INTERNAL;
        };
    }

    private String mapTitle(int status) {
        return switch (status) {
            case 401 -> "Unauthorized";
            case 404 -> "Not found";
            case 409 -> "Conflict";
            default -> "Error";
        };
    }
}


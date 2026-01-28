package com.sistema.common.api.error;

import com.sistema.common.api.trace.TraceIdProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CommonIllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        String instance = ErrorResponseFactory.resolveInstance(uriInfo);
        String errorCode = CommonErrorCodeResolver.validationErrorCode(instance);
        ErrorResponse response = ErrorResponseFactory.build(
                CommonErrorTypes.VALIDATION,
                "Validation error",
                400,
                exception.getMessage(),
                instance,
                errorCode,
                null,
                traceIdProvider
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}


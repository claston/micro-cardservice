package com.sistema.common.api.error;

import com.sistema.common.api.trace.TraceIdProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class CommonThrowableMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = Logger.getLogger(CommonThrowableMapper.class);

    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(Throwable exception) {
        String traceId = traceIdProvider.getTraceId();
        String instance = ErrorResponseFactory.resolveInstance(uriInfo);
        String errorCode = CommonErrorCodeResolver.internalErrorCode(instance);
        LOG.errorf(exception, "unhandled error traceId=%s instance=%s", traceId, instance);
        ErrorResponse response = ErrorResponseFactory.build(
                CommonErrorTypes.INTERNAL,
                "Internal server error",
                500,
                "Unexpected error",
                instance,
                errorCode,
                null,
                traceIdProvider
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}


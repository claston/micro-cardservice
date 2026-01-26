package com.sistema.wallet.api.error;

import com.sistema.wallet.api.trace.TraceIdProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class WalletThrowableMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOG = Logger.getLogger(WalletThrowableMapper.class);

    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(Throwable exception) {
        String traceId = traceIdProvider.getTraceId();
        LOG.errorf(exception, "wallet unhandled error traceId=%s", traceId);
        ErrorResponse response = ErrorResponseFactory.build(
                WalletErrorTypes.INTERNAL,
                "Internal server error",
                500,
                "Unexpected error",
                ErrorResponseFactory.resolveInstance(uriInfo),
                WalletErrorCodes.WALLET_INTERNAL_ERROR,
                null,
                traceIdProvider
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
    }
}

package com.sistema.wallet.api.error;

import com.sistema.wallet.api.trace.TraceIdProvider;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WalletIllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ErrorResponse response = ErrorResponseFactory.build(
                WalletErrorTypes.VALIDATION,
                "Validation error",
                400,
                exception.getMessage(),
                ErrorResponseFactory.resolveInstance(uriInfo),
                WalletErrorCodes.WALLET_VALIDATION_ERROR,
                null,
                traceIdProvider
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}

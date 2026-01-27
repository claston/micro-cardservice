package com.sistema.wallet.api.error;

import com.sistema.wallet.api.trace.TraceIdProvider;
import com.sistema.wallet.application.exception.WalletException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class WalletExceptionMapper implements ExceptionMapper<WalletException> {
    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(WalletException exception) {
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
            case 401 -> WalletErrorTypes.UNAUTHORIZED;
            case 404 -> WalletErrorTypes.NOT_FOUND;
            case 409 -> WalletErrorTypes.CONFLICT;
            default -> WalletErrorTypes.INTERNAL;
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

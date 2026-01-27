package com.sistema.wallet.api.trace;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.util.UUID;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class RequestTraceFilter implements ContainerRequestFilter, ContainerResponseFilter {
    public static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String TRACE_ID_PROPERTY = "traceId";
    private static final Logger LOG = Logger.getLogger(RequestTraceFilter.class);

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String traceId = requestContext.getHeaderString(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        traceIdProvider.setTraceId(traceId);
        requestContext.setProperty(TRACE_ID_PROPERTY, traceId);
        LOG.debugf("wallet request traceId=%s path=%s", traceId, requestContext.getUriInfo().getPath());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object traceId = requestContext.getProperty(TRACE_ID_PROPERTY);
        if (traceId == null) {
            traceId = traceIdProvider.getTraceId();
        }
        responseContext.getHeaders().putSingle(TRACE_ID_HEADER, traceId.toString());
    }
}

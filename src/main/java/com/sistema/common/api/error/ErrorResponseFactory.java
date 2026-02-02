package com.sistema.common.api.error;

import com.sistema.common.api.trace.TraceIdProvider;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;

public final class ErrorResponseFactory {
    private ErrorResponseFactory() {
    }

    public static ErrorResponse build(String type,
                                      String title,
                                      int status,
                                      String detail,
                                      String instance,
                                      String errorCode,
                                      List<ErrorViolation> violations,
                                      TraceIdProvider traceIdProvider) {
        return build(type, title, status, detail, instance, errorCode, violations, null, traceIdProvider);
    }

    public static ErrorResponse build(String type,
                                      String title,
                                      int status,
                                      String detail,
                                      String instance,
                                      String errorCode,
                                      List<ErrorViolation> violations,
                                      Map<String, Object> meta,
                                      TraceIdProvider traceIdProvider) {
        ErrorResponse response = new ErrorResponse();
        response.setType(type);
        response.setTitle(title);
        response.setStatus(status);
        response.setDetail(detail);
        response.setInstance(instance);
        response.setErrorCode(errorCode);
        response.setViolations(violations);
        response.setMeta(meta);
        response.setTraceId(traceIdProvider.getTraceId());
        return response;
    }

    public static String resolveInstance(UriInfo uriInfo) {
        if (uriInfo == null || uriInfo.getRequestUri() == null) {
            return null;
        }
        return uriInfo.getRequestUri().getPath();
    }
}


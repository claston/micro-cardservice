package com.sistema.common.api.error;

import com.sistema.common.api.trace.TraceIdProvider;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class CommonValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Context
    UriInfo uriInfo;

    @Inject
    TraceIdProvider traceIdProvider;

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<ErrorViolation> violations = exception.getConstraintViolations()
                .stream()
                .map(this::toViolation)
                .sorted(Comparator.comparing(ErrorViolation::getField, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());

        String instance = ErrorResponseFactory.resolveInstance(uriInfo);
        String errorCode = CommonErrorCodeResolver.validationErrorCode(instance);

        ErrorResponse response = ErrorResponseFactory.build(
                CommonErrorTypes.VALIDATION,
                "Validation error",
                400,
                "Validation failed",
                instance,
                errorCode,
                violations,
                traceIdProvider
        );

        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    private ErrorViolation toViolation(ConstraintViolation<?> violation) {
        String field = extractField(violation);
        Object rejected = violation.getInvalidValue();
        return new ErrorViolation(field, violation.getMessage(), rejected);
    }

    private String extractField(ConstraintViolation<?> violation) {
        if (violation.getPropertyPath() == null) {
            return null;
        }
        String path = violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        if (lastDot >= 0 && lastDot + 1 < path.length()) {
            return path.substring(lastDot + 1);
        }
        return path;
    }
}


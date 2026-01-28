package com.sistema.common.api.error;

public final class CommonErrorCodeResolver {
    private CommonErrorCodeResolver() {
    }

    public static String validationErrorCode(String instancePath) {
        String path = normalize(instancePath);
        if (path.startsWith("customers")) {
            return "CUSTOMER_VALIDATION_ERROR";
        }
        if (path.startsWith("accounts") || path.startsWith("transfers")) {
            return "WALLET_VALIDATION_ERROR";
        }
        return "VALIDATION_ERROR";
    }

    public static String internalErrorCode(String instancePath) {
        String path = normalize(instancePath);
        if (path.startsWith("customers")) {
            return "CUSTOMER_INTERNAL_ERROR";
        }
        if (path.startsWith("accounts") || path.startsWith("transfers")) {
            return "WALLET_INTERNAL_ERROR";
        }
        return "INTERNAL_ERROR";
    }

    private static String normalize(String instancePath) {
        if (instancePath == null) {
            return "";
        }
        String trimmed = instancePath.trim();
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        return trimmed;
    }
}


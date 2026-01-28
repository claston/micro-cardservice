package com.sistema.customer.application;

public final class DocumentNumberNormalizer {
    private DocumentNumberNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String digitsOnly = raw.replaceAll("\\D", "");
        return digitsOnly.isBlank() ? null : digitsOnly;
    }
}


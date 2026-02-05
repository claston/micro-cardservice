package com.sistema.payments.api.error;

public final class PaymentsErrorCodes {
    public static final String PAYMENTS_UNAUTHORIZED = "PAYMENTS_UNAUTHORIZED";
    public static final String PAYMENTS_WEBHOOK_UNAUTHORIZED = "PAYMENTS_WEBHOOK_UNAUTHORIZED";
    public static final String PAYMENTS_NOT_FOUND = "PAYMENTS_NOT_FOUND";
    public static final String PAYMENTS_IDEMPOTENCY_CONFLICT = "PAYMENTS_IDEMPOTENCY_CONFLICT";
    public static final String PAYMENTS_INVALID_STATE = "PAYMENTS_INVALID_STATE";
    public static final String PAYMENTS_INSUFFICIENT_BALANCE = "PAYMENTS_INSUFFICIENT_BALANCE";
    public static final String PAYMENTS_PROVIDER_ERROR = "PAYMENTS_PROVIDER_ERROR";
    public static final String PAYMENTS_INTERNAL_ERROR = "PAYMENTS_INTERNAL_ERROR";

    private PaymentsErrorCodes() {
    }
}

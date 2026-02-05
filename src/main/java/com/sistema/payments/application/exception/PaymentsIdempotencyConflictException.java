package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

import java.util.Map;
import java.util.UUID;

public class PaymentsIdempotencyConflictException extends PaymentsException {
    public PaymentsIdempotencyConflictException(String idempotencyKey, UUID paymentId) {
        super("Idempotency conflict", 409, PaymentsErrorCodes.PAYMENTS_IDEMPOTENCY_CONFLICT,
                Map.of("idempotencyKey", idempotencyKey, "paymentId", paymentId));
    }
}

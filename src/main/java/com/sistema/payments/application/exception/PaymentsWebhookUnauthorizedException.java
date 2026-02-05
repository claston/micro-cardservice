package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsWebhookUnauthorizedException extends PaymentsException {
    public PaymentsWebhookUnauthorizedException(String message) {
        super(message, 401, PaymentsErrorCodes.PAYMENTS_WEBHOOK_UNAUTHORIZED);
    }
}

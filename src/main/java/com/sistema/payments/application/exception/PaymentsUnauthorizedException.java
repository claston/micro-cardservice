package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsUnauthorizedException extends PaymentsException {
    public PaymentsUnauthorizedException(String message) {
        super(message, 401, PaymentsErrorCodes.PAYMENTS_UNAUTHORIZED);
    }
}

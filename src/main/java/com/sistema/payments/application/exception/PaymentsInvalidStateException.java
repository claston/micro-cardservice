package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsInvalidStateException extends PaymentsException {
    public PaymentsInvalidStateException(String message) {
        super(message, 409, PaymentsErrorCodes.PAYMENTS_INVALID_STATE);
    }
}

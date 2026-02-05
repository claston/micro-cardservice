package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsNotFoundException extends PaymentsException {
    public PaymentsNotFoundException(String message) {
        super(message, 404, PaymentsErrorCodes.PAYMENTS_NOT_FOUND);
    }
}

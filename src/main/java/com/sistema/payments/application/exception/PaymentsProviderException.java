package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsProviderException extends PaymentsException {
    public PaymentsProviderException(String message) {
        super(message, 500, PaymentsErrorCodes.PAYMENTS_PROVIDER_ERROR);
    }
}

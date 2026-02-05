package com.sistema.payments.application.exception;

import com.sistema.payments.api.error.PaymentsErrorCodes;

public class PaymentsInsufficientBalanceException extends PaymentsException {
    public PaymentsInsufficientBalanceException(String message) {
        super(message, 409, PaymentsErrorCodes.PAYMENTS_INSUFFICIENT_BALANCE);
    }
}

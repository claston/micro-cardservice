package com.sistema.customer.application.exception;

import com.sistema.customer.api.error.CustomerErrorCodes;

public class CustomerUnauthorizedException extends CustomerException {
    public CustomerUnauthorizedException(String message) {
        super(message, 401, CustomerErrorCodes.CUSTOMER_UNAUTHORIZED);
    }
}


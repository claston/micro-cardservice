package com.sistema.customer.application.exception;

import com.sistema.customer.api.error.CustomerErrorCodes;

import java.util.UUID;

public class CustomerNotFoundException extends CustomerException {
    public CustomerNotFoundException(UUID customerId) {
        super("customer not found: " + customerId, 404, CustomerErrorCodes.CUSTOMER_NOT_FOUND);
    }
}


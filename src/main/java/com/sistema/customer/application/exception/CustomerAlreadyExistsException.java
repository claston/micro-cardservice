package com.sistema.customer.application.exception;

import com.sistema.customer.api.error.CustomerErrorCodes;

public class CustomerAlreadyExistsException extends CustomerException {
    public CustomerAlreadyExistsException() {
        super("customer already exists for this document", 409, CustomerErrorCodes.CUSTOMER_ALREADY_EXISTS);
    }
}


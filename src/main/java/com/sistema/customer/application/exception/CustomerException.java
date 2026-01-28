package com.sistema.customer.application.exception;

public abstract class CustomerException extends RuntimeException {
    private final int status;
    private final String errorCode;

    protected CustomerException(String message, int status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}


package com.sistema.wallet.application.exception;

public abstract class WalletException extends RuntimeException {
    private final int status;
    private final String errorCode;

    protected WalletException(String message, int status, String errorCode) {
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

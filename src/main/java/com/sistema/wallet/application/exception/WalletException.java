package com.sistema.wallet.application.exception;

public abstract class WalletException extends RuntimeException {
    private final int status;
    private final String errorCode;
    private final java.util.Map<String, Object> meta;

    protected WalletException(String message, int status, String errorCode) {
        this(message, status, errorCode, null);
    }

    protected WalletException(String message, int status, String errorCode, java.util.Map<String, Object> meta) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.meta = meta;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public java.util.Map<String, Object> getMeta() {
        return meta;
    }
}

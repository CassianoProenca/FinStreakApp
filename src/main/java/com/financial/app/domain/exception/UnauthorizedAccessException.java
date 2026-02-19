package com.financial.app.domain.exception;

public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

package com.luckymarket.domain.exception.auth;

public class RedisException extends RuntimeException {
    private final RedisErrorCode errorCode;

    public RedisException(RedisErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorCode.getMessage();
    }
}

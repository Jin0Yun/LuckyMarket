package com.luckymarket.user.exception;

public class LoginException extends RuntimeException {
    private final LoginErrorCode errorCode;

    public LoginException(LoginErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}

package com.luckymarket.user.exception;

public class SignupException extends RuntimeException {
    private final SignupErrorCode errorCode;

    public SignupException(SignupErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}

package com.luckymarket.user.exception;

public enum UserErrorCode {
    NAME_BLANK("이름은 필수 입력값입니다.");

    private final String message;

    UserErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

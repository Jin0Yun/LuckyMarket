package com.luckymarket.user.exception;

public enum UserErrorCode {
    NAME_BLANK("이름은 필수 입력값입니다."),
    PHONE_NUMBER_BLANK("전화번호는 필수 입력값입니다."),
    INVALID_PHONE_NUMBER("유효하지 않은 전화번호 형식입니다."),
    INVALID_PHONE_NUMBER_FORMAT("전화번호 형식이 잘못되었습니다."),
    ADDRESS_BLANK("주소는 필수 입력값입니다.");

    private final String message;

    UserErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

package com.luckymarket.domain.exception.user;

public enum UserErrorCode {
    NAME_BLANK("이름은 필수 입력값입니다."),
    PHONE_NUMBER_BLANK("전화번호는 필수 입력값입니다."),
    INVALID_PHONE_NUMBER_FORMAT("전화번호 형식이 잘못되었습니다."),
    INVALID_PHONE_NUMBER("유효하지 않은 전화번호 형식입니다."),
    ADDRESS_BLANK("주소는 필수 입력값입니다."),

    USER_ALREADY_DELETED("이 사용자는 이미 탈퇴한 상태입니다."),
    UNAUTHORIZED_ACCESS("사용자가 접근 권한이 없습니다."),

    PASSWORD_TOO_SHORT("비밀번호는 8자 이상이어야 합니다."),
    PASSWORD_MISSING_SPECIAL_CHAR("비밀번호에는 특수문자가 포함되어야 합니다."),
    PASSWORD_MISSING_LOWERCASE("비밀번호에는 소문자가 포함되어야 합니다."),
    PASSWORD_MISSING_UPPERCASE("비밀번호에는 대문자가 포함되어야 합니다."),
    PASSWORD_BLANK("비밀번호는 필수 항목입니다.");

    private final String message;

    UserErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

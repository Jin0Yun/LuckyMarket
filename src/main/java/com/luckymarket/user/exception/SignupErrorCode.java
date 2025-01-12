package com.luckymarket.user.exception;

public enum SignupErrorCode {
    PASSWORD_TOO_SHORT("비밀번호는 8자 이상이어야 합니다."),
    PASSWORD_MISSING_SPECIAL_CHAR("비밀번호에는 특수문자가 포함되어야 합니다."),
    PASSWORD_MISSING_LOWERCASE("비밀번호에는 소문자가 포함되어야 합니다."),
    PASSWORD_MISSING_UPPERCASE("비밀번호에는 대문자가 포함되어야 합니다."),
    PASSWORD_BLANK("비밀번호는 필수 항목입니다."),
    EMAIL_ALREADY_USED("이미 사용 중인 이메일입니다."),
    INVALID_EMAIL_FORMAT("잘못된 이메일 형식입니다."),
    EMAIL_BLANK("이메일은 필수 항목입니다.");

    private final String message;

    SignupErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

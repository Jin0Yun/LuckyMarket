package com.luckymarket.auth.exception;

public enum AuthErrorCode {
    EMAIL_NOT_FOUND("이메일을 찾을 수 없습니다."),
    EMAIL_BLANK("이메일은 필수 입력 값입니다."),
    INVALID_EMAIL_FORMAT("잘못된 이메일 형식입니다."),

    PASSWORD_MISMATCH("비밀번호가 일치하지 않습니다."),
    PASSWORD_BLANK("비밀번호는 필수 입력 값입니다."),

    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    MALFORMED_TOKEN("잘못된 토큰 형식입니다."),
    TOKEN_SIGNATURE_INVALID("토큰 서명이 유효하지 않습니다."),

    JWT_AUTHENTICATION_FAILED("JWT 인증에 실패했습니다.");

    private final String message;

    AuthErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

package com.luckymarket.domain.exception.auth;

public enum RedisErrorCode {
    REFRESH_TOKEN_SAVE_FAILED("리프레시 토큰 저장에 실패했습니다."),
    REFRESH_TOKEN_NOT_FOUND("리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_DELETE_FAILED("리프레시 토큰 삭제에 실패했습니다."),
    INVALID_REFRESH_TOKEN("리프레시 토큰이 유효하지 않습니다."),

    BLACKLIST_TOKEN_SAVE_FAILED("블랙리스트 토큰 저장에 실패했습니다."),
    BLACKLIST_TOKEN_NOT_FOUND("블랙리스트 토큰을 찾을 수 없습니다."),
    BLACKLIST_TOKEN_DELETE_FAILED("블랙리스트 토큰 삭제에 실패했습니다."),

    KEY_EXIST_CHECK_FAILED("키 존재 여부 확인 중 오류가 발생했습니다."),

    ALREADY_LOGGED_IN_OTHER_DEVICE("다른 디바이스에서 이미 로그인 되어 있습니다."),
    KEY_SAVE_FAILED("로그인 상태 저장에 실패했습니다."),
    KEY_DELETE_FAILED("로그인 상태 삭제에 실패했습니다.");

    private final String message;

    RedisErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

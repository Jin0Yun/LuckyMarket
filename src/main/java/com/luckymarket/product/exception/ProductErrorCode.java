package com.luckymarket.product.exception;

public enum ProductErrorCode {
    TITLE_BLANK("상품 제목은 필수 입력값입니다."),
    DESCRIPTION_BLANK("상품 설명은 필수 입력값입니다."),
    PRICE_BLANK("상품 가격은 필수 입력값입니다."),
    INVALID_PRICE("상품 가격은 0보다 커야 합니다."),
    CATEGORY_BLANK("상품 카테고리는 필수 입력값입니다."),
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    MAX_PARTICIPANTS_INVALID("최대 참여 인원은 0보다 커야 합니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DATE_INVALID("유효하지 않은 종료일입니다."),
    PRODUCT_ALREADY_EXISTS("이미 존재하는 상품입니다."),
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다."),
    UNAUTHORIZED_PRODUCT_MODIFY("상품 수정 권한이 없습니다.");

    private final String message;

    ProductErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

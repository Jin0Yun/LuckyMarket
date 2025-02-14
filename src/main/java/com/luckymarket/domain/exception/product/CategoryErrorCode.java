package com.luckymarket.domain.exception.product;

public enum CategoryErrorCode {
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    CATEGORY_CODE_INVALID("유효하지 않은 카테고리 코드입니다."),
    PARENT_CATEGORY_NOT_FOUND("부모 카테고리를 찾을 수 없습니다."),
    NO_SUBCATEGORY_FOUND("하위 카테고리가 없습니다."),
    CATEGORY_NAME_BLANK("카테고리 이름은 필수 입력값입니다."),
    CATEGORY_CODE_BLANK("카테고리 코드는 필수 입력값입니다."),
    CATEGORY_CODE_NOT_FOUND("카테고리 코드를 찾을 수 없습니다.");

    private final String message;

    CategoryErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

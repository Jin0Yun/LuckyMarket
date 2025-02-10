package com.luckymarket.domain.exception.product;

public class CategoryException extends RuntimeException {
    private final CategoryErrorCode errorCode;

    public CategoryException(CategoryErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}

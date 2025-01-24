package com.luckymarket.product.exception;

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

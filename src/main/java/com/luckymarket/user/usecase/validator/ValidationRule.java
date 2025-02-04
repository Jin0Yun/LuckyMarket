package com.luckymarket.user.usecase.validator;

public interface ValidationRule<T> {
    void validate(T value);
}

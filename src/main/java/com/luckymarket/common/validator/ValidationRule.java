package com.luckymarket.common.validator;

public interface ValidationRule<T> {
    void validate(T value);
}

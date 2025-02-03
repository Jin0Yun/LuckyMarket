package com.luckymarket.user.validator;

public interface ValidationRule<T> {
    void validate(T value);
}

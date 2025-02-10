package com.luckymarket.application.validation;

public interface ValidationRule<T> {
    void validate(T value);
}

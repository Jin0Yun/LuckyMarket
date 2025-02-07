package com.luckymarket.auth.validator;

public interface AuthValidationService {
    void validateEmail(String email);
    void validatePassword(String password);
}

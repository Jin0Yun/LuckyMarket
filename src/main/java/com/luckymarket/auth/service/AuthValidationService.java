package com.luckymarket.auth.service;

public interface AuthValidationService {
    void validateEmail(String email);
    void validatePassword(String password);
    void validateLoginPassword(String password);
}

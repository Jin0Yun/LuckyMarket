package com.luckymarket.application.validation.auth;

import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import org.springframework.stereotype.Component;

@Component
public class LoginPasswordValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.PASSWORD_BLANK);
        }
    }
}
package com.luckymarket.auth.validator;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.common.validator.ValidationRule;
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
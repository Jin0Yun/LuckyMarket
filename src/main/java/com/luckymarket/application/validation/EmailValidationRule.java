package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.EMAIL_BLANK);
        }
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
}

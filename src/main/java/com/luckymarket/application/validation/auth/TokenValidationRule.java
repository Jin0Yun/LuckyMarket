package com.luckymarket.application.validation.auth;

import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import org.springframework.stereotype.Component;

@Component
public class TokenValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}

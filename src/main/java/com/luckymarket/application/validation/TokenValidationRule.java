package com.luckymarket.application.validation;

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

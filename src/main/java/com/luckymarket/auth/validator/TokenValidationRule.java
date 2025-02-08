package com.luckymarket.auth.validator;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.common.validator.ValidationRule;
import org.springframework.stereotype.Component;

@Component
public class TokenValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }
}

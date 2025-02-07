package com.luckymarket.common.validator;

import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new UserException(UserErrorCode.PASSWORD_BLANK);
        }

        if (password.length() < 8) {
            throw new UserException(UserErrorCode.PASSWORD_TOO_SHORT);
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new UserException(UserErrorCode.PASSWORD_MISSING_UPPERCASE);
        }

        if (!password.matches(".*[a-z].*")) {
            throw new UserException(UserErrorCode.PASSWORD_MISSING_LOWERCASE);
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new UserException(UserErrorCode.PASSWORD_MISSING_SPECIAL_CHAR);
        }
    }
}

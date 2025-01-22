package com.luckymarket.user.validator;

import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;

public class PasswordValidator {
    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new SignupException(SignupErrorCode.PASSWORD_BLANK);
        }

        if (password.length() < 8) {
            throw new SignupException(SignupErrorCode.PASSWORD_TOO_SHORT);
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_UPPERCASE);
        }

        if (!password.matches(".*[a-z].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_LOWERCASE);
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new SignupException(SignupErrorCode.PASSWORD_MISSING_SPECIAL_CHAR);
        }
    }
}

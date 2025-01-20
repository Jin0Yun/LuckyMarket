package com.luckymarket.auth.service.login;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LoginValidator {
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.EMAIL_BLANK);
        }

        if (!isValidEmailFormat(email)) {
            throw new AuthException(AuthErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new AuthException(AuthErrorCode.PASSWORD_BLANK);
        }
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }
}

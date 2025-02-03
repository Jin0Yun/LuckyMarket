package com.luckymarket.user.validator;

import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new UserException(UserErrorCode.EMAIL_BLANK);
        }
        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", email)) {
            throw new UserException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }
}

package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new UserException(UserErrorCode.PHONE_NUMBER_BLANK);
        }
        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new UserException(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }
    }
}

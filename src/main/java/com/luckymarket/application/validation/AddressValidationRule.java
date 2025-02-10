package com.luckymarket.application.validation;

import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.springframework.stereotype.Component;

@Component
public class AddressValidationRule implements ValidationRule<String> {
    @Override
    public void validate(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new UserException(UserErrorCode.ADDRESS_BLANK);
        }
    }
}

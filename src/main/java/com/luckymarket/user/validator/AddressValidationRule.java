package com.luckymarket.user.validator;

import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
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

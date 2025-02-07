package com.luckymarket.user.usecase.validator;

import com.luckymarket.common.validator.ValidationRule;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
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

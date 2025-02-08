package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.common.validator.PasswordValidationRule;
import com.luckymarket.common.validator.ValidationRule;
import com.luckymarket.user.usecase.service.MemberValidationService;
import com.luckymarket.user.usecase.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberValidationServiceImpl implements MemberValidationService {
    private final ValidationRule<Long> userValidationRule;
    private final ValidationRule<String> phoneNumberValidationRule;
    private final ValidationRule<String> passwordValidationRule;
    private final ValidationRule<String> addressValidationRule;

    @Autowired
    public MemberValidationServiceImpl(
            UserValidationRule userValidator,
            PhoneNumberValidationRule phoneNumberValidator,
            PasswordValidationRule passwordValidator,
            AddressValidationRule addressValidator
    ) {
        this.userValidationRule = userValidator;
        this.phoneNumberValidationRule = phoneNumberValidator;
        this.passwordValidationRule = passwordValidator;
        this.addressValidationRule = addressValidator;
    }

    @Override
    public void validateUser(Long userId) {
        userValidationRule.validate(userId);
    }

    @Override
    public void validatePassword(String password) {
        passwordValidationRule.validate(password);
    }

    @Override
    public void validatePhoneNumber(String phoneNumber) {
        phoneNumberValidationRule.validate(phoneNumber);
    }

    public void validateAddress(String address) {
        addressValidationRule.validate(address);
    }
}

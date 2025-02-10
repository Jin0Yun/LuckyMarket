package com.luckymarket.application.service.user.impl;

import com.luckymarket.application.validation.AddressValidationRule;
import com.luckymarket.application.validation.PhoneNumberValidationRule;
import com.luckymarket.application.validation.UserValidationRule;
import com.luckymarket.application.validation.PasswordValidationRule;
import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.application.service.user.MemberValidationService;
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

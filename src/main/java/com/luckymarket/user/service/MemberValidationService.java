package com.luckymarket.user.service;

import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberValidationService {
    private final ValidationRule<String> phoneNumberValidationRule;
    private final ValidationRule<String> emailValidationRule;
    private final ValidationRule<String> passwordValidationRule;
    private final ValidationRule<String> addressValidationRule;

    @Autowired
    public MemberValidationService(
            PhoneNumberValidationRule phoneNumberValidator,
            EmailValidationRule emailValidator,
            PasswordValidationRule passwordValidator,
            AddressValidationRule addressValidator
    ) {
        this.phoneNumberValidationRule = phoneNumberValidator;
        this.emailValidationRule = emailValidator;
        this.passwordValidationRule = passwordValidator;
        this.addressValidationRule = addressValidator;
    }

    public void validateSignupFields(SignupRequestDto dto) {
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
    }

    public void validateEmail(String email) {
        emailValidationRule.validate(email);
    }

    public void validatePassword(String password) {
        passwordValidationRule.validate(password);
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumberValidationRule.validate(phoneNumber);
        }
    }

    public void validateAddress(String address) {
        if (address != null) {
            addressValidationRule.validate(address);
        }
    }
}

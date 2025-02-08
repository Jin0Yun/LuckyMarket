package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.service.AuthValidationService;
import com.luckymarket.auth.validator.LoginPasswordValidationRule;
import com.luckymarket.auth.validator.EmailValidationRule;
import com.luckymarket.common.validator.PasswordValidationRule;
import com.luckymarket.common.validator.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthValidationServiceImpl implements AuthValidationService {
    private final ValidationRule<String> emailValidationRule;
    private final ValidationRule<String> passwordValidationRule;
    private final ValidationRule<String> loginPasswordValidationRule;

    @Autowired
    public AuthValidationServiceImpl(
            EmailValidationRule emailValidator,
            PasswordValidationRule passwordValidator,
            LoginPasswordValidationRule loginPasswordValidationRule
    ) {
        this.emailValidationRule = emailValidator;
        this.passwordValidationRule = passwordValidator;
        this.loginPasswordValidationRule = loginPasswordValidationRule;
    }

    @Override
    public void validateEmail(String email) {
        emailValidationRule.validate(email);
    }

    @Override
    public void validatePassword(String password) {
        passwordValidationRule.validate(password);
    }

    @Override
    public void validateLoginPassword(String password) {
        loginPasswordValidationRule.validate(password);
    }
}

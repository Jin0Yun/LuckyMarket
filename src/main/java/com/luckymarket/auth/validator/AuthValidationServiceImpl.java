package com.luckymarket.auth.validator;

import com.luckymarket.common.validator.EmailValidationRule;
import com.luckymarket.common.validator.PasswordValidationRule;
import com.luckymarket.common.validator.ValidationRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthValidationServiceImpl implements AuthValidationService {
    private final ValidationRule<String> emailValidationRule;
    private final ValidationRule<String> passwordValidationRule;

    @Autowired
    public AuthValidationServiceImpl(
            EmailValidationRule emailValidator,
            PasswordValidationRule passwordValidator
    ) {
        this.emailValidationRule = emailValidator;
        this.passwordValidationRule = passwordValidator;
    }

    @Override
    public void validateEmail(String email) {
        emailValidationRule.validate(email);
    }

    @Override
    public void validatePassword(String password) {
        passwordValidationRule.validate(password);
    }
}

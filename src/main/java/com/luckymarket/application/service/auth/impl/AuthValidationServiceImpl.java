package com.luckymarket.application.service.auth.impl;

import com.luckymarket.application.service.auth.AuthValidationService;
import com.luckymarket.application.validation.auth.LoginPasswordValidationRule;
import com.luckymarket.application.validation.user.EmailValidationRule;
import com.luckymarket.application.validation.auth.MemberValidationRule;
import com.luckymarket.application.validation.auth.TokenValidationRule;
import com.luckymarket.application.validation.user.PasswordValidationRule;
import com.luckymarket.application.validation.ValidationRule;
import com.luckymarket.domain.entity.user.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthValidationServiceImpl implements AuthValidationService {
    private final ValidationRule<String> emailValidationRule;
    private final ValidationRule<String> passwordValidationRule;
    private final ValidationRule<String> loginPasswordValidationRule;
    private final ValidationRule<Member> memberValidationRule;
    private final ValidationRule<String> tokenValidationRule;

    @Autowired
    public AuthValidationServiceImpl(
            EmailValidationRule emailValidator,
            PasswordValidationRule passwordValidator,
            LoginPasswordValidationRule loginPasswordValidationRule,
            MemberValidationRule memberValidationRule,
            TokenValidationRule tokenValidationRule
    ) {
        this.emailValidationRule = emailValidator;
        this.passwordValidationRule = passwordValidator;
        this.loginPasswordValidationRule = loginPasswordValidationRule;
        this.memberValidationRule = memberValidationRule;
        this.tokenValidationRule = tokenValidationRule;
    }

    @Override
    public void validateLogin(String email, String password) {
        emailValidationRule.validate(email);
        loginPasswordValidationRule.validate(password);
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

    @Override
    public void validateMember(Member member) {
        memberValidationRule.validate(member);
    }

    @Override
    public void validateToken(String token) {
        tokenValidationRule.validate(token);
    }
}

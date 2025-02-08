package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.service.AuthValidationService;
import com.luckymarket.auth.validator.LoginPasswordValidationRule;
import com.luckymarket.auth.validator.EmailValidationRule;
import com.luckymarket.auth.validator.MemberValidationRule;
import com.luckymarket.auth.validator.TokenValidationRule;
import com.luckymarket.common.validator.PasswordValidationRule;
import com.luckymarket.common.validator.ValidationRule;
import com.luckymarket.user.domain.model.Member;
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

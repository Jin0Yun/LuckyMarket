package com.luckymarket.application.service.auth;

import com.luckymarket.domain.entity.user.Member;

public interface AuthValidationService {
    void validateEmail(String email);
    void validatePassword(String password);
    void validateLogin(String email, String password);
    void validateLoginPassword(String password);
    void validateMember(Member member);
    void validateToken(String token);
}

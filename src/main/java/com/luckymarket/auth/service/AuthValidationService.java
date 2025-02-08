package com.luckymarket.auth.service;

import com.luckymarket.user.domain.model.Member;

public interface AuthValidationService {
    void validateEmail(String email);
    void validatePassword(String password);
    void validateLoginPassword(String password);
    void validateMember(Member member);
    void validateToken(String token);
}

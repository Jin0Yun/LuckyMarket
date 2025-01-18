package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;

public interface AuthService {
    Member login(String email, String password);
    String generateToken(Member member);
}

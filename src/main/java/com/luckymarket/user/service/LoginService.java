package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;

public interface LoginService {
    Member login(String email, String password);
    String generateToken(Member member);
}

package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;

public interface AuthService {
    TokenResponseDto login(String email, String password);
    void logout(String accessToken);
    TokenResponseDto refreshAccessToken(String accessToken);
}

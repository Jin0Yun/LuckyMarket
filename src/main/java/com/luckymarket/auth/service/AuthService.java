package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String accessToken);
    TokenResponseDto refreshAccessToken(String accessToken);
}

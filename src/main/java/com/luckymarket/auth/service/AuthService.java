package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.user.usecase.dto.SignupRequestDto;

public interface AuthService {
    void signup(SignupRequestDto signupRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String accessToken);
    TokenResponseDto refreshAccessToken(String accessToken);
}

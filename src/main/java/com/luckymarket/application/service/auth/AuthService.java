package com.luckymarket.application.service.auth;

import com.luckymarket.application.dto.auth.LoginRequestDto;
import com.luckymarket.application.dto.auth.LoginResponseDto;
import com.luckymarket.application.dto.auth.TokenResponseDto;
import com.luckymarket.application.dto.auth.SignupRequestDto;

public interface AuthService {
    void signup(SignupRequestDto signupRequestDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto);
    void logout(String accessToken);
    TokenResponseDto refreshAccessToken(String accessToken);
}

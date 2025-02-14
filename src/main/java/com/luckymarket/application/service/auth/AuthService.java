package com.luckymarket.application.service.auth;

import com.luckymarket.application.dto.auth.AuthLoginRequest;
import com.luckymarket.application.dto.auth.AuthTokenResponse;
import com.luckymarket.application.dto.auth.AuthRefreshTokenResponse;
import com.luckymarket.application.dto.auth.SignupRequest;

public interface AuthService {
    void signup(SignupRequest signupRequest);
    AuthTokenResponse login(AuthLoginRequest authLoginRequest);
    void logout(String accessToken);
    AuthRefreshTokenResponse refreshAccessToken(String accessToken);
}

package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final TokenService tokenService;

    public AuthServiceImpl(LoginService loginService, LogoutService logoutService, TokenService tokenService) {
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResponseDto login(String email, String password) {
        return loginService.login(email, password);
    }

    @Override
    public void logout(String accessToken) {
        logoutService.logout(accessToken);
    }

    @Override
    public TokenResponseDto refreshAccessToken(String accessToken) {
        return tokenService.refreshAccessToken(accessToken);
    }
}

package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.service.login.LoginService;
import com.luckymarket.auth.service.logout.LogoutService;
import com.luckymarket.auth.service.token.TokenService;
import com.luckymarket.user.usecase.dto.SignupRequestDto;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final SignupService signupService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final TokenService tokenService;

    public AuthServiceImpl(
            SignupService signupService,
            LoginService loginService,
            LogoutService logoutService,
            TokenService tokenService
    ) {
        this.signupService = signupService;
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.tokenService = tokenService;
    }

    @Override
    public void signup(SignupRequestDto signupRequestDto) {
        signupService.signup(signupRequestDto);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        return loginService.login(loginRequestDto);
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

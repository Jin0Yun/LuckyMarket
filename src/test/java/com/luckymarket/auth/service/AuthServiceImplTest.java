package com.luckymarket.auth.service;

import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.service.login.LoginService;
import com.luckymarket.auth.service.logout.LogoutService;
import com.luckymarket.auth.service.token.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private LoginService loginService;

    @Mock
    private LogoutService logoutService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("로그인 메서드가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallLoginServiceWhenLogin() {
        // given
        String email = "test@gmail.com";
        String password = "password";
        TokenResponseDto mockToken = new TokenResponseDto("mockAccessToken");
        when(loginService.login(email, password)).thenReturn(mockToken);

        // when
        TokenResponseDto result = authService.login(email, password);

        // then
        verify(loginService, times(1)).login(email, password);
    }

    @DisplayName("로그아웃 메서드가 호출되는지 확인하는 메서드")
    @Test
    void shouldCallLogoutServiceWhenLogout() {
        // given
        String accessToken = "Bearer valid-access-token";

        // when
        authService.logout(accessToken);

        // then
        verify(logoutService, times(1)).logout(accessToken);
    }

    @DisplayName("엑세스 토큰 재발급 메서드가 호출되는지 확인하는 메서드")
    @Test
    void shouldCallTokenServiceWhenRefreshAccessToken() {
        // given
        String accessToken = "Bearer valid-access-token";
        TokenResponseDto mockToken = new TokenResponseDto("newAccessToken");
        when(tokenService.refreshAccessToken(accessToken)).thenReturn(mockToken);

        // when
        TokenResponseDto result = authService.refreshAccessToken(accessToken);

        // then
        verify(tokenService, times(1)).refreshAccessToken(accessToken);
    }
}

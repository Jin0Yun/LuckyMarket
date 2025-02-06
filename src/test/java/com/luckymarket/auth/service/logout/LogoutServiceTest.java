package com.luckymarket.auth.service.logout;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LogoutServiceTest {
    @Mock
    private RedisService redisService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private LogoutService logoutService;

    private String accessToken;
    private String token;
    private Long userId;

    @BeforeEach
    void setUp() {
        accessToken = "Bearer valid-jwt-token";
        token = "valid-jwt-token";
        userId = 1L;
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("로그아웃 시 토큰이 블랙리스트에 추가되고 리프레시 토큰이 삭제되는지 테스트")
    @Test
    void shouldBlacklistTokenAndDeleteRefreshToken() {
        // given
        when(jwtTokenProvider.getSubject(token)).thenReturn(userId.toString());
        when(redisService.isBlacklisted(token)).thenReturn(false);

        // when
        logoutService.logout(accessToken);

        // then
        verify(redisService).addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        verify(redisService).removeRefreshToken(userId);
    }

    @DisplayName("이미 블랙리스트에 있는 토큰으로 로그아웃 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenIsAlreadyBlacklisted() {
        // given
        when(jwtTokenProvider.getSubject(token)).thenReturn(userId.toString());
        when(redisService.isBlacklisted(token)).thenReturn(true);

        // when & then
        assertThrows(AuthException.class, () -> logoutService.logout(accessToken));
    }

    @DisplayName("잘못된 형식의 토큰이 주어졌을 때 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() {
        // given
        String invalidToken = "Bearer invalid-jwt-token";
        when(jwtTokenProvider.getSubject(invalidToken.replace("Bearer ", "").trim())).thenThrow(new AuthException(AuthErrorCode.INVALID_TOKEN));

        // when & then
        assertThrows(AuthException.class, () -> logoutService.logout(invalidToken));  // 예외가 발생하는지 검증
    }
}

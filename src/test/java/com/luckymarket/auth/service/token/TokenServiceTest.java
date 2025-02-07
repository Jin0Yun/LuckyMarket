package com.luckymarket.auth.service.token;

import com.luckymarket.auth.dto.TokenResponseDto;
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

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("리프레시 토큰이 유효할 경우 새로운 액세스 토큰을 반환하는지 테스트")
    @Test
    void shouldReturnNewAccessTokenWhenRefreshTokenIsValid() {
        // given
        String refreshToken = "valid-refresh-token";
        Long userId = 1L;

        // Mock behavior
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getSubject(refreshToken)).thenReturn(userId.toString());
        when(redisService.getRefreshToken(userId)).thenReturn(Optional.of(refreshToken));
        when(jwtTokenProvider.createAccessToken(userId)).thenReturn("new-access-token");

        // when
        TokenResponseDto result = tokenService.refreshAccessToken(refreshToken);

        // then
        assertEquals("new-access-token", result.getAccessToken());
    }

    @DisplayName("리프레시 토큰이 유효하지 않을 경우 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
        // given
        String refreshToken = "Bearer expired-access-token";
        when(jwtTokenProvider.validateToken(refreshToken)).thenThrow(new AuthException(AuthErrorCode.INVALID_TOKEN));

        // when & then
        assertThrows(AuthException.class, () -> tokenService.refreshAccessToken(refreshToken));
    }
}

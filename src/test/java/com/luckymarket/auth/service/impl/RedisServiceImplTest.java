package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.RedisKeyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RedisServiceImplTest {
    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    private Long userId;
    private String refreshToken;
    private String accessToken;
    private long expiration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = 2342L;
        refreshToken = "refresh-token";
        accessToken = "blacklist-token";
        expiration = 1000L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @DisplayName("리프레시 토큰을 Redis에 성공적으로 저장하는지 테스트")
    @Test
    void shouldSaveRefreshTokenSuccessfully() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(valueOperations.setIfAbsent(refreshTokenKey, refreshToken, expiration, TimeUnit.MILLISECONDS)).thenReturn(true);

        // when
        redisService.saveRefreshToken(userId, refreshToken, expiration);

        // then
        verify(valueOperations).setIfAbsent(refreshTokenKey, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    @DisplayName("리프레시 토큰 저장 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenSaveRefreshTokenFails() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(valueOperations.setIfAbsent(refreshTokenKey, refreshToken, expiration, TimeUnit.MILLISECONDS)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.saveRefreshToken(userId, refreshToken, expiration));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("리프레시 토큰을 Redis에서 조회되는지 테스트")
    @Test
    void shouldGetRefreshTokenSuccessfully() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(valueOperations.get(refreshTokenKey)).thenReturn(refreshToken);

        // when
        Optional<String> result = redisService.getRefreshToken(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(refreshToken);
    }

    @DisplayName("리프레시 토큰이 없을 경우 빈 Optional을 반환하는지 테스트")
    @Test
    void shouldReturnEmptyWhenRefreshTokenNotFound() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(valueOperations.get(refreshTokenKey)).thenReturn(null);

        // when
        Optional<String> result = redisService.getRefreshToken(userId);

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("리프레시 토큰을 Redis에서 삭제되는지 테스트")
    @Test
    void shouldRemoveRefreshTokenSuccessfully() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(redisTemplate.delete(refreshTokenKey)).thenReturn(true);

        // when
        redisService.removeRefreshToken(userId);

        // then
        verify(redisTemplate).delete(refreshTokenKey);
    }

    @DisplayName("리프레시 토큰 삭제 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenRemoveRefreshTokenFails() {
        // given
        String refreshTokenKey = RedisKeyUtils.getRefreshTokenKey(userId);
        when(redisTemplate.delete(refreshTokenKey)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.removeRefreshToken(userId));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트 토큰을 Redis에 성공적으로 저장하는지 테스트")
    @Test
    void shouldAddToBlacklistSuccessfully() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(valueOperations.setIfAbsent(blacklistKey, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS)).thenReturn(true);

        // when
        redisService.addToBlacklist(accessToken, expiration);

        // then
        verify(valueOperations).setIfAbsent(blacklistKey, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);
    }

    @DisplayName("블랙리스트 토큰 저장 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenAddToBlacklistFails() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(valueOperations.setIfAbsent(blacklistKey, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.addToBlacklist(accessToken, expiration));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트에 토큰이 있는지 확인하는지 테스트")
    @Test
    void shouldReturnTrueWhenTokenIsBlacklisted() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(true);

        // when
        boolean result = redisService.isBlacklisted(accessToken);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("블랙리스트에 토큰이 없을 경우 false를 반환하는지 테스트")
    @Test
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(redisTemplate.hasKey(blacklistKey)).thenReturn(false);

        // when
        boolean result = redisService.isBlacklisted(accessToken);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("블랙리스트에서 토큰 삭제 성공하는지 테스트")
    @Test
    void shouldRemoveFromBlacklistSuccessfully() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(redisTemplate.delete(blacklistKey)).thenReturn(true);

        // when
        redisService.removeFromBlacklist(accessToken);

        // then
        verify(redisTemplate).delete(blacklistKey);
    }

    @DisplayName("블랙리스트에서 토큰 삭제 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenRemoveFromBlacklistFails() {
        // given
        String blacklistKey = RedisKeyUtils.getBlacklistKey(accessToken);
        when(redisTemplate.delete(blacklistKey)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.removeFromBlacklist(accessToken));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED.getMessage());
    }
}

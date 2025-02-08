package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
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
        when(valueOperations.setIfAbsent("refreshToken-user: " + userId, refreshToken, expiration, TimeUnit.MILLISECONDS)).thenReturn(true);

        // when
        redisService.saveRefreshToken(userId, refreshToken, expiration);

        // then
        verify(valueOperations).setIfAbsent("refreshToken-user: " + userId, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    @DisplayName("리프레시 토큰 저장 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenSaveRefreshTokenFails() {
        // given
        when(valueOperations.setIfAbsent("refreshToken-user: " + userId, refreshToken)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.saveRefreshToken(userId, refreshToken, expiration));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("리프레시 토큰을 Redis에서 조회되는지 테스트")
    @Test
    void shouldGetRefreshTokenSuccessfully() {
        // given
        when(valueOperations.get("refreshToken-user: " + userId)).thenReturn(refreshToken);

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
        when(valueOperations.get("refreshToken-user: " + userId)).thenReturn(null);

        // when
        Optional<String> result = redisService.getRefreshToken(userId);

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("리프레시 토큰을 Redis에서 삭제되는지 테스트")
    @Test
    void shouldRemoveRefreshTokenSuccessfully() {
        // given
        when(redisTemplate.delete("refreshToken-user: " + userId)).thenReturn(true);

        // when
        redisService.removeRefreshToken(userId);

        // then
        verify(redisTemplate).delete("refreshToken-user: " + userId);
    }

    @DisplayName("리프레시 토큰 삭제 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenRemoveRefreshTokenFails() {
        // given
        when(redisTemplate.delete("refreshToken-user: " + userId)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.removeRefreshToken(userId));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트에 토큰을 성공적으로 추가하는지 테스트")
    @Test
    void shouldAddToBlacklistSuccessfully() {
        // given
        when(valueOperations.setIfAbsent(accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS)).thenReturn(true);

        // when
        redisService.addToBlacklist(accessToken, expiration);

        // then
        verify(valueOperations).setIfAbsent(accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);
    }

    @DisplayName("블랙리스트 토큰 조회 성공 시 true 반환하는지 테스트")
    @Test
    void shouldReturnTrueWhenAccessTokenIsBlacklisted() {
        // given
        when(redisTemplate.hasKey(accessToken)).thenReturn(true);

        // when
        boolean result = redisService.isBlacklisted(accessToken);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("블랙리스트 추가 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenAddToBlacklistFails() {
        // given
        when(valueOperations.setIfAbsent(accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.addToBlacklist(accessToken, expiration));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트에서 토큰을 삭제하는지 테스트")
    @Test
    void shouldRemoveFromBlacklistSuccessfully() {
        // given
        when(redisTemplate.delete(accessToken)).thenReturn(true);

        // when
        redisService.removeFromBlacklist(accessToken);

        // then
        verify(redisTemplate).delete(accessToken);
    }

    @DisplayName("블랙리스트에서 토큰 삭제 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenRemoveFromBlacklistFails() {
        // given
        when(redisTemplate.delete(accessToken)).thenReturn(false);

        // when & then
        RedisException exception = assertThrows(RedisException.class, () -> redisService.removeFromBlacklist(accessToken));
        assertThat(exception.getMessage()).isEqualTo(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED.getMessage());
    }
}

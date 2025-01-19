package com.luckymarket.auth.service;

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
import static org.mockito.Mockito.*;

class RedisServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisServiceImpl redisService;

    private String userId;
    private String refreshToken;
    private String accessToken;
    private long expiration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = "user123";
        refreshToken = "refresh-token";
        accessToken = "blacklist-token";
        expiration = 1000L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @DisplayName("리프레시 토큰을 Redis에 성공적으로 저장하는지 테스트")
    @Test
    void shouldSaveRefreshTokenSuccessfully() {
        // given
        when(valueOperations.setIfAbsent("refresh:" + userId, refreshToken)).thenReturn(true);

        // when
        redisService.saveRefreshToken(userId, refreshToken, expiration);

        // then
        assertThat(valueOperations.setIfAbsent("refresh:" + userId, refreshToken)).isTrue();
        assertThat(redisTemplate.expire("refresh:" + userId, expiration, TimeUnit.MILLISECONDS)).isNotNull();
    }

    @DisplayName("리프레시 토큰 저장 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenSaveRefreshTokenFails() {
        // given
        when(valueOperations.setIfAbsent("refresh:" + userId, refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> redisService.saveRefreshToken(userId, refreshToken, expiration))
                .isInstanceOf(RedisException.class)
                .hasMessage(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("리프레시 토큰을 Redis에서 성공적으로 조회하는지 테스트")
    @Test
    void shouldGetRefreshTokenSuccessfully() {
        // given
        when(valueOperations.get("refresh:" + userId)).thenReturn(refreshToken);

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
        when(valueOperations.get("refresh:" + userId)).thenReturn(null);

        // when
        Optional<String> result = redisService.getRefreshToken(userId);

        // then
        assertThat(result).isNotPresent();
    }

    @DisplayName("리프레시 토큰을 Redis에서 성공적으로 삭제하는지 테스트")
    @Test
    void shouldDeleteRefreshTokenSuccessfully() {
        // given
        when(redisTemplate.delete("refresh:" + userId)).thenReturn(true);

        // when
        redisService.deleteRefreshToken(userId);

        // then
        assertThat(redisTemplate.delete("refresh:" + userId)).isTrue();
    }

    @DisplayName("리프레시 토큰 삭제 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenDeleteRefreshTokenFails() {
        // given
        when(redisTemplate.delete("refresh:" + userId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> redisService.deleteRefreshToken(userId))
                .isInstanceOf(RedisException.class)
                .hasMessage(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트에 토큰을 성공적으로 추가하는지 테스트")
    @Test
    void shouldAddToBlacklistSuccessfully() {
        // given
        doNothing().when(valueOperations).set("blacklist:" + accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);

        // when
        redisService.addToBlacklist(accessToken, expiration);

        // then
        verify(valueOperations).set("blacklist:" + accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);
    }

    @DisplayName("블랙리스트 토큰 추가 실패 시 예외를 던지는지 테스트")
    @Test
    void shouldThrowExceptionWhenAddToBlacklistFails() {
        // given
        doThrow(new RuntimeException("Redis error")).when(valueOperations).set("blacklist:" + accessToken, "BLACKLISTED", expiration, TimeUnit.MILLISECONDS);

        // when & then
        assertThatThrownBy(() -> redisService.addToBlacklist(accessToken, expiration))
                .isInstanceOf(RedisException.class)
                .hasMessage(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED.getMessage());
    }

    @DisplayName("블랙리스트에 토큰이 있는지 확인하는지 테스트")
    @Test
    void shouldCheckIfTokenIsBlacklisted() {
        // given
        when(redisTemplate.hasKey("blacklist:" + accessToken)).thenReturn(true);

        // when
        boolean result = redisService.isBlacklisted(accessToken);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("블랙리스트에 토큰이 없는 경우 확인하는지 테스트")
    @Test
    void shouldReturnFalseWhenTokenIsNotBlacklisted() {
        // given
        when(redisTemplate.hasKey("blacklist:" + accessToken)).thenReturn(false);

        // when
        boolean result = redisService.isBlacklisted(accessToken);

        // then
        assertThat(result).isFalse();
    }
}

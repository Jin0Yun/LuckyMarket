package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent("refreshToken-user: " + userId, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
            if (!Boolean.TRUE.equals(success)) {
                throw new RedisException(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public Optional<String> getRefreshToken(Long userId) {
        try {
            String token = redisTemplate.opsForValue().get("refreshToken-user: " + userId);
            return Optional.ofNullable(token);
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
    }

    @Override
    public void removeRefreshToken(Long userId) {
        try {
            boolean deleted = redisTemplate.delete("refreshToken-user: " + userId);
            if (!deleted) {
                throw new RedisException(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.REFRESH_TOKEN_DELETE_FAILED);
        }
    }

    @Override
    public void addToBlacklist(String accessToken, long expirationTime) {
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(accessToken, "BLACKLISTED", expirationTime, TimeUnit.MILLISECONDS);
            if (!Boolean.TRUE.equals(success)) {
                throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_SAVE_FAILED);
        }
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        try {
            return redisTemplate.hasKey(accessToken);
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.KEY_EXIST_CHECK_FAILED);
        }
    }

    @Override
    public void removeFromBlacklist(String accessToken) {
        try {
            boolean deleted = redisTemplate.delete(accessToken);
            if (!deleted) {
                throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.BLACKLIST_TOKEN_DELETE_FAILED);
        }
    }

    public void markUserAsLoggedIn(Long userId) {
        try {
            if (redisTemplate.hasKey("user-logged-in:" + userId)) {
                throw new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE);
            }

            Boolean success = redisTemplate.opsForValue().setIfAbsent("user-logged-in:" + userId, "true");
            if (!Boolean.TRUE.equals(success)) {
                throw new RedisException(RedisErrorCode.KEY_SAVE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.KEY_SAVE_FAILED);
        }
    }


    public void markUserAsLoggedOut(Long userId) {
        try {
            boolean deleted = redisTemplate.delete("user-logged-in:" + userId);
            if (!deleted) {
                throw new RedisException(RedisErrorCode.KEY_DELETE_FAILED);
            }
        } catch (Exception e) {
            throw new RedisException(RedisErrorCode.KEY_DELETE_FAILED);
        }
    }
}

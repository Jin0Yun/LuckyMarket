package com.luckymarket.auth.security;

import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    boolean validateToken(String token);
    Authentication getAuthentication(String token);
    String createAccessToken(Long userId);
    String createRefreshToken(Long userId);
    String getSubject(String token);
    long getRemainingExpirationTime(String token);
}
package com.luckymarket.auth.security;

import org.springframework.security.core.Authentication;

public interface JwtTokenProvider {
    boolean validateToken(String token);
    String createAccessToken(Long userId, String email);
    String createRefreshToken(Long userId, String email);
    String getSubject(String token);
    Authentication getAuthentication(String token);
    long getRemainingExpirationTime(String token);
}
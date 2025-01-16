package com.luckymarket.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    public String createToken(String email) {
        return null;
    }

    public boolean validateToken(String token) {
        return true;
    }

    public String getEmailFromToken(String token) {
        return null;
    }

    public Authentication getAuthentication(String token) {
        return null;
    }
}

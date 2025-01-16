package com.luckymarket.security;

import com.luckymarket.user.exception.LoginErrorCode;
import com.luckymarket.user.exception.LoginException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new LoginException(LoginErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new LoginException(LoginErrorCode.INVALID_TOKEN);
        } catch (SignatureException e) {
            throw new LoginException(LoginErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            throw new LoginException(LoginErrorCode.INVALID_TOKEN);
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new LoginException(LoginErrorCode.TOKEN_PARSING_FAILED);
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        User principal = new User(email, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}

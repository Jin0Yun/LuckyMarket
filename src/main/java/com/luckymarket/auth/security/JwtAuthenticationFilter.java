package com.luckymarket.auth.security;

import com.luckymarket.auth.exception.AuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (token != null) {
                validateAndAuthenticateToken(token);
            }
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            handleAuthError(response, e);
        } catch (Exception e) {
            handleUnexpectedError(response, e);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private void validateAndAuthenticateToken(String token) {
        jwtTokenProvider.validateToken(token);
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private void handleAuthError(HttpServletResponse response, AuthException e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("인증에 실패했습니다. 다시 로그인해주세요.");
    }

    private void handleUnexpectedError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("서버 내부 오류가 발생했습니다.");
    }
}

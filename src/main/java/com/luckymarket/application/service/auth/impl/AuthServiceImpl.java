package com.luckymarket.application.service.auth.impl;

import com.luckymarket.application.dto.auth.LoginRequestDto;
import com.luckymarket.application.dto.auth.LoginResponseDto;
import com.luckymarket.application.dto.auth.TokenResponseDto;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.exception.auth.RedisErrorCode;
import com.luckymarket.domain.exception.auth.RedisException;
import com.luckymarket.infrastructure.security.JwtTokenProvider;
import com.luckymarket.infrastructure.security.SecurityContextService;
import com.luckymarket.application.service.auth.AuthService;
import com.luckymarket.infrastructure.redis.RedisService;
import com.luckymarket.application.dto.auth.SignupRequestDto;
import com.luckymarket.application.service.auth.AuthValidationService;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.adapter.out.persistence.user.UserRepository;
import com.luckymarket.application.service.user.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AuthValidationService authValidationService;
    private final SecurityContextService securityContextService;

    @Override
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        authValidationService.validateEmail(signupRequestDto.getEmail());
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_USED);
        }
        authValidationService.validatePassword(signupRequestDto.getPassword());
        String encodedPassword = passwordService.encodePassword(signupRequestDto.getPassword());

        Member member = createMember(signupRequestDto, encodedPassword);
        userRepository.save(member);
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authValidationService.validateLogin(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        Member member = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        authValidationService.validateMember(member);
        passwordService.matches(loginRequestDto.getPassword(), member.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), loginRequestDto.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), loginRequestDto.getEmail());

        saveLoginInfo(member, refreshToken);
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public void logout(String accessToken) {
        String token = extractToken(accessToken);
        Long userId = securityContextService.getCurrentUserId();
        redisService.addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));

        String refreshToken = redisService.getRefreshToken(userId)
                .orElseThrow(() -> new RedisException(RedisErrorCode.REFRESH_TOKEN_NOT_FOUND));
        saveLogoutInfo(userId, refreshToken);
    }

    @Override
    public TokenResponseDto refreshAccessToken(String refreshToken) {
        String token = extractToken(refreshToken);
        jwtTokenProvider.validateToken(token);

        Long userId = securityContextService.getCurrentUserId();
        String redisRefreshToken = redisService.getRefreshToken(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (!redisRefreshToken.equals(token)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        String email = member.getEmail();

        String newAccessToken = jwtTokenProvider.createAccessToken(userId, email);
        return new TokenResponseDto(newAccessToken);
    }

    private Member createMember(SignupRequestDto signupRequestDto, String encodedPassword) {
        Member member = new Member();
        member.setEmail(signupRequestDto.getEmail());
        member.setPassword(encodedPassword);
        member.setUsername(signupRequestDto.getUsername());
        return member;
    }

    private void saveLoginInfo(Member member, String refreshToken) {
        redisService.markUserAsLoggedIn(member.getId());
        redisService.saveRefreshToken(
                member.getId(),
                refreshToken,
                jwtTokenProvider.getRemainingExpirationTime(refreshToken)
        );
        member.setLastLogin(LocalDateTime.now());
        userRepository.save(member);
    }

    private void saveLogoutInfo(Long userId, String refreshToken) {
        redisService.addToBlacklist(refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));
        redisService.markUserAsLoggedOut(userId);
        redisService.removeRefreshToken(userId);
    }

    private String extractToken(String refreshToken) {
        authValidationService.validateToken(refreshToken);
        return refreshToken.substring(7).trim();
    }
}

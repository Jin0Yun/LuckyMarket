package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.exception.RedisErrorCode;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.security.SecurityContextService;
import com.luckymarket.auth.service.AuthService;
import com.luckymarket.auth.service.RedisService;
import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.auth.service.AuthValidationService;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final AuthValidationService authValidationService;
    private final SecurityContextService securityContextService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService,
            AuthValidationService authValidationService,
            SecurityContextService securityContextService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.authValidationService = authValidationService;
        this.securityContextService = securityContextService;
    }

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
        authValidationService.validateEmail(loginRequestDto.getEmail());
        authValidationService.validateLoginPassword(loginRequestDto.getPassword());

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
        redisService.addToBlacklist(refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));
        redisService.markUserAsLoggedOut(userId);
        redisService.removeRefreshToken(userId);
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

    private String extractToken(String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        return refreshToken.substring(7).trim();
    }
}

package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.AuthService;
import com.luckymarket.auth.service.RedisService;
import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.auth.service.AuthValidationService;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
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

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService,
            AuthValidationService authValidationService
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.authValidationService = authValidationService;
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
        Member member = new Member();
        member.setEmail(signupRequestDto.getEmail());
        member.setPassword(encodedPassword);
        userRepository.save(member);
    }

    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authValidationService.validateEmail(loginRequestDto.getEmail());
        authValidationService.validateLoginPassword(loginRequestDto.getPassword());

        Member member = userRepository.findByEmail(loginRequestDto.getEmail());
        if (member == null) {
            throw new AuthException(AuthErrorCode.EMAIL_NOT_FOUND);
        }

        if (member.getStatus() == Status.DELETED) {
            throw new AuthException(AuthErrorCode.USER_ALREADY_DELETED);
        }

        passwordService.matches(loginRequestDto.getPassword(), member.getPassword());
        try {
            redisService.markUserAsLoggedIn(member.getId());
        } catch (RedisException e) {
            throw new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        redisService.saveRefreshToken(member.getId(), refreshToken, jwtTokenProvider.getRemainingExpirationTime(refreshToken));
        member.setLastLogin(LocalDateTime.now());
        userRepository.save(member);
        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public void logout(String accessToken) {
        String token = accessToken.replace("Bearer ", "").trim();
        Long userId = Long.parseLong(jwtTokenProvider.getSubject(token));

        if (redisService.isBlacklisted(token)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        redisService.addToBlacklist(token, jwtTokenProvider.getRemainingExpirationTime(token));
        redisService.markUserAsLoggedOut(userId);
        redisService.removeRefreshToken(userId);
    }

    @Override
    public TokenResponseDto refreshAccessToken(String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);

        Long userId = Long.parseLong(jwtTokenProvider.getSubject(refreshToken));
        String redisRefreshToken = redisService.getRefreshToken(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        if (!redisRefreshToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);
        return new TokenResponseDto(newAccessToken);
    }
}

package com.luckymarket.auth.service.login;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.exception.RedisException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final LoginValidator loginValidator;

    public LoginService(
            UserRepository userRepository,
            PasswordService passwordService,
            JwtTokenProvider jwtTokenProvider,
            RedisService redisService,
            LoginValidator loginValidator
    ) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisService = redisService;
        this.loginValidator = loginValidator;
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        loginValidator.validateEmail(loginRequestDto.getEmail());
        loginValidator.validatePassword(loginRequestDto.getPassword());

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
}


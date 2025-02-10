package com.luckymarket.application.service.auth.impl;

import com.luckymarket.application.dto.auth.LoginRequestDto;
import com.luckymarket.application.dto.auth.LoginResponseDto;
import com.luckymarket.application.dto.auth.SignupRequestDto;
import com.luckymarket.application.dto.auth.TokenResponseDto;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.infrastructure.security.JwtTokenProvider;
import com.luckymarket.infrastructure.security.SecurityContextService;
import com.luckymarket.application.service.auth.AuthValidationService;
import com.luckymarket.infrastructure.redis.RedisService;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.adapter.out.persistence.user.UserRepository;
import com.luckymarket.application.service.user.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisService redisService;

    @Mock
    private AuthValidationService authValidationService;

    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");
    }

    @DisplayName("회원가입 시 유효한 이메일과 비밀번호를 검증하고 회원을 저장한다.")
    @Test
    void signup_ShouldSaveMember_WhenValidData() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "ValidPassword123!", "userA");

        doNothing().when(authValidationService).validateEmail(signupRequestDto.getEmail());
        doNothing().when(authValidationService).validatePassword(signupRequestDto.getPassword());
        when(userRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(false);
        when(passwordService.encodePassword(signupRequestDto.getPassword())).thenReturn("encodedPassword");

        // when
        authService.signup(signupRequestDto);

        // then
        verify(userRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("이미 사용 중인 이메일로 회원가입 시 예외를 던진다.")
    @Test
    void signup_ShouldThrowException_WhenEmailAlreadyUsed() {
        // given
        SignupRequestDto signupRequestDto = new SignupRequestDto("test@example.com", "ValidPassword123!", "userA");

        doNothing().when(authValidationService).validateEmail(signupRequestDto.getEmail());
        doNothing().when(authValidationService).validatePassword(signupRequestDto.getPassword());
        when(userRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(true);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.signup(signupRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EMAIL_ALREADY_USED.getMessage());
    }

    @DisplayName("로그인 시 유효한 이메일과 비밀번호로 로그인 후 토큰을 반환한다.")
    @Test
    void login_ShouldReturnTokens_WhenValidData() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "ValidPassword123!");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(member));
        when(jwtTokenProvider.createAccessToken(member.getId(), member.getEmail())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(member.getId(), member.getEmail())).thenReturn("refreshToken");

        // when
        LoginResponseDto loginResponse = authService.login(loginRequestDto);

        // then
        assertEquals("accessToken", loginResponse.getAccessToken());
        assertEquals("refreshToken", loginResponse.getRefreshToken());
    }

    @DisplayName("로그인 시 이메일을 찾을 수 없으면 예외를 던진다.")
    @Test
    void login_ShouldThrowException_WhenEmailNotFound() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "ValidPassword123!");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertEquals(AuthErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @DisplayName("로그인 시 비밀번호가 일치하지 않으면 예외를 던진다.")
    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "InvalidPassword123!");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(member));
        doThrow(new AuthException(AuthErrorCode.PASSWORD_MISMATCH)).when(passwordService).matches(loginRequestDto.getPassword(), member.getPassword());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("로그인 시 이미 다른 디바이스에서 로그인 중이면 예외를 던진다.")
    @Test
    void login_ShouldThrowException_WhenUserAlreadyLoggedInOtherDevice() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "ValidPassword123!");
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(Optional.of(member));
        doThrow(new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE)).when(redisService).markUserAsLoggedIn(member.getId());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE.getMessage());
    }

    @DisplayName("로그아웃 시 유효한 토큰으로 로그아웃을 처리한다.")
    @Test
    void logout_ShouldLogOutUser_WhenValidAccessToken() {
        // given
        String accessToken = "Bearer validAccessToken";
        String token = "validAccessToken";
        String refreshToken = "validRefreshToken";

        when(securityContextService.getCurrentUserId()).thenReturn(member.getId());
        when(redisService.isBlacklisted(token)).thenReturn(false);
        when(redisService.getRefreshToken(member.getId())).thenReturn(Optional.of(refreshToken));

        // when
        authService.logout(accessToken);

        // then
        verify(redisService).markUserAsLoggedOut(member.getId());
        verify(redisService).removeRefreshToken(member.getId());
    }

    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 생성한다.")
    @Test
    void refreshAccessToken_ShouldReturnNewAccessToken_WhenValidRefreshToken() {
        // given
        String refreshToken = "Bearer validRefreshToken";
        String token = "validRefreshToken";
        String newAccessToken = "newAccessToken";

        when(securityContextService.getCurrentUserId()).thenReturn(member.getId());
        when(redisService.getRefreshToken(member.getId())).thenReturn(Optional.of(token));
        when(userRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(jwtTokenProvider.createAccessToken(member.getId(), member.getEmail())).thenReturn(newAccessToken);

        // when
        TokenResponseDto tokenResponse = authService.refreshAccessToken(refreshToken);

        // then
        assertThat(tokenResponse.getAccessToken()).isEqualTo(newAccessToken);
        verify(jwtTokenProvider).createAccessToken(member.getId(), member.getEmail());
    }

    @DisplayName("리프레시 토큰이 없으면 예외를 던진다.")
    @Test
    void refreshAccessToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        // given
        String refreshToken = "invalidRefreshToken";
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getSubject(refreshToken)).thenReturn("1");
        when(redisService.getRefreshToken(1L)).thenReturn(Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.refreshAccessToken(refreshToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("사용자를 찾을 수 없는 경우 예외가 발생한다.")
    @Test
    void refreshAccessToken_ShouldThrowException_WhenUserNotFound() {
        // given
        String refreshToken = "Bearer validRefreshToken";
        String token = "validRefreshToken";
        Long userId = 1L;

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(redisService.getRefreshToken(userId)).thenReturn(Optional.of(token));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class,
                () -> authService.refreshAccessToken(refreshToken));

        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.USER_NOT_FOUND.getMessage());
        verify(jwtTokenProvider, never()).createAccessToken(anyLong(), anyString());
    }
}

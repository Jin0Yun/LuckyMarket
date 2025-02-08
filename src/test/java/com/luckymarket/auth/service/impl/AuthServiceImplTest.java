package com.luckymarket.auth.service.impl;

import com.luckymarket.auth.dto.LoginRequestDto;
import com.luckymarket.auth.dto.LoginResponseDto;
import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.auth.dto.TokenResponseDto;
import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.security.JwtTokenProvider;
import com.luckymarket.auth.service.AuthValidationService;
import com.luckymarket.auth.service.RedisService;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.PasswordService;
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

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(member);
        when(jwtTokenProvider.createAccessToken(member.getId())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(member.getId())).thenReturn("refreshToken");

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

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(null);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.EMAIL_NOT_FOUND.getMessage());
    }

    @DisplayName("로그인 시 비밀번호가 일치하지 않으면 예외를 던진다.")
    @Test
    void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
        // given
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "InvalidPassword123!");

        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(member);
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
        when(userRepository.findByEmail(loginRequestDto.getEmail())).thenReturn(member);
        doThrow(new AuthException(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE)).when(redisService).markUserAsLoggedIn(member.getId());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.ALREADY_LOGGED_IN_OTHER_DEVICE.getMessage());
    }

    @DisplayName("로그아웃 시 토큰이 블랙리스트에 있을 경우 예외를 던진다.")
    @Test
    void logout_ShouldThrowException_WhenTokenIsBlacklisted() {
        // given
        String accessToken = "Bearer invalidToken";
        when(jwtTokenProvider.getSubject(anyString())).thenReturn("1");
        when(redisService.isBlacklisted(accessToken.replace("Bearer ", "").trim())).thenReturn(true);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.logout(accessToken));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("로그아웃 시 유효한 토큰으로 로그아웃을 처리한다.")
    @Test
    void logout_ShouldLogOutUser_WhenValidAccessToken() {
        // given
        String accessToken = "Bearer validToken";
        when(jwtTokenProvider.getSubject(anyString())).thenReturn("1");
        when(redisService.isBlacklisted(accessToken.replace("Bearer ", "").trim())).thenReturn(false);

        // when
        authService.logout(accessToken);

        // then
        verify(redisService, times(1)).addToBlacklist(anyString(), anyLong());
        verify(redisService, times(1)).markUserAsLoggedOut(anyLong());
    }

    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 생성한다.")
    @Test
    void refreshAccessToken_ShouldReturnNewAccessToken_WhenValidRefreshToken() {
        // given
        String refreshToken = "validRefreshToken";
        Long userId = 1L;
        when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
        when(jwtTokenProvider.getSubject(refreshToken)).thenReturn(String.valueOf(userId));
        when(redisService.getRefreshToken(userId)).thenReturn(java.util.Optional.of(refreshToken));
        when(jwtTokenProvider.createAccessToken(userId)).thenReturn("newAccessToken");

        // when
        TokenResponseDto tokenResponse = authService.refreshAccessToken(refreshToken);

        // then
        assertEquals("newAccessToken", tokenResponse.getAccessToken());
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
}

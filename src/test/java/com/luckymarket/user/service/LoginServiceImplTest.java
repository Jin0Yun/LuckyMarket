package com.luckymarket.user.service;

import com.luckymarket.security.JwtTokenProvider;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.LoginRequestDto;
import com.luckymarket.user.exception.LoginErrorCode;
import com.luckymarket.user.exception.LoginException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class LoginServiceImplTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginServiceImpl loginService;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 회원 정보 초기화
        mockMember = Member.builder()
                .email("luckymarket@gmail.com")
                .password("LuckyMarket123!!")
                .username("testuser")
                .build();
    }

    @DisplayName("존재하지 않는 이메일로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("nonexistent@example.com");
        dto.setPassword("password");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.EMAIL_NOT_FOUND.getMessage());
    }

    @DisplayName("잘못된 비밀번호로 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword("wrongPassword");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(mockMember);
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(false);

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @DisplayName("이메일이 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail(" ");
        dto.setPassword("LuckyMarket123!!");

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("비밀번호가 빈 값일 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword(" ");

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("이메일 형식이 잘못된 경우 로그인 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("invalid-email");
        dto.setPassword("LuckyMarket123!!");

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(dto.getEmail(), dto.getPassword()));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("로그인 성공 시 사용자 정보를 반환하는지 테스트")
    @Test
    void shouldReturnUserWhenLoginIsSuccessful() {
        // given
        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("luckymarket@gmail.com");
        dto.setPassword("LuckyMarket123!!");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(mockMember);
        when(passwordEncoder.matches(dto.getPassword(), mockMember.getPassword())).thenReturn(true);

        // when
        Member result = loginService.login(dto.getEmail(), dto.getPassword());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(mockMember.getEmail());
        assertThat(result.getPassword()).isEqualTo(mockMember.getPassword());
    }

    @DisplayName("로그인 성공 후 JWT 토큰이 생성되는지 확인하는 테스트")
    @Test
    void shouldReturnJWTWhenLoginIsSuccessful() {
        // given
        String expectedToken = "jwt-token";
        when(userRepository.findByEmail(mockMember.getEmail())).thenReturn(mockMember);
        when(passwordEncoder.matches(mockMember.getPassword(), mockMember.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(mockMember.getEmail())).thenReturn(expectedToken);

        // when
        String token = loginService.generateToken(mockMember);

        // then
        assertThat(token).isEqualTo(expectedToken);
    }

    @DisplayName("만료된 JWT 토큰으로 인증 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenIsExpired() {
        // given
        String expectedToken = "jwt-token";
        when(jwtTokenProvider.validateToken(expectedToken)).thenThrow(new LoginException(LoginErrorCode.EXPIRED_TOKEN));

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.generateToken(mockMember));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.EXPIRED_TOKEN.getMessage());
    }

    @DisplayName("잘못된 서명을 가진 JWT 토큰 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenHasInvalidSignature() {
        // given
        String invalidSignatureToken = "invalid-signature";
        when(jwtTokenProvider.validateToken(invalidSignatureToken)).thenThrow(new LoginException(LoginErrorCode.INVALID_TOKEN));
        
        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.generateToken(mockMember));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.INVALID_TOKEN.getMessage());
    }

    @DisplayName("잘못된 형식의 JWT 토큰 시 예외를 반환하는지 테스트")
    @Test
    void shouldThrowExceptionWhenTokenHasInvalidFormat() {
        // given
        String invalidFormatToken = "invalid-format";
        when(jwtTokenProvider.validateToken(invalidFormatToken)).thenThrow(new LoginException(LoginErrorCode.INVALID_TOKEN));

        // when & then
        LoginException exception = assertThrows(LoginException.class, () -> loginService.generateToken(mockMember));
        assertThat(exception.getMessage()).isEqualTo(LoginErrorCode.INVALID_TOKEN.getMessage());
    }
}

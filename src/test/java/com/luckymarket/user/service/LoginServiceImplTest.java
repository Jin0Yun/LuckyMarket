package com.luckymarket.user.service;

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

    @DisplayName("존재하지 않는 이메일로 로그인 시 예외 처리 테스트")
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

    @DisplayName("잘못된 비밀번호로 로그인 시 예외 처리 테스트")
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

    @DisplayName("이메일이 빈 값일 경우 로그인 시 예외 처리 테스트")
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

    @DisplayName("비밀번호가 빈 값일 경우 로그인 시 예외 처리 테스트")
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

    @DisplayName("이메일 형식이 잘못된 경우 로그인 시 예외 처리 테스트")
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
        assertThat(result.getUsername()).isEqualTo(mockMember.getUsername());
    }
}

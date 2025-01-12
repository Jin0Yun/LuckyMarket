package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SignupServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SignupServiceImpl signupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private SignupRequestDto createTestSignupRequestDto() {
        return SignupRequestDto
                .builder()
                .email("test@test.com")
                .password("ValidPassword123!")
                .username("testuser")
                .build();
    }

    @DisplayName("유효한 정보를 입력해서 회원가입이 성공하는지 확인하는 테스트")
    @Test
    void shouldSignUpSuccessfullyWithValidInfo() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();

        // when
        Member member = signupService.signup(dto);

        // then
        assertThat(member.getEmail()).isEqualTo("test@test.com");
        assertThat(member.getPassword()).isEqualTo("ValidPassword123!");
        assertThat(member.getUsername()).isEqualTo("testuser");
    }

    @DisplayName("Role 기본값으로 저장되는지 확인하는 테스트")
    @Test
    void shouldSaveWithDefaultRole() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        Member member = dto.toEntity(dto.getEmail());

        // when
        signupService.signup(dto);

        // then
        assertThat(member.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("Status 기본값으로 저장되는지 확인하는 테스트")
    @Test
    void shouldSaveWithDefaultStatus() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        Member member = dto.toEntity(dto.getEmail());

        // when
        signupService.signup(dto);

        // then
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("회원가입 시 이메일이 공백인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setEmail(" ");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 이메일이 누락되었는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailIsMissing() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setEmail(null);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 이메일이 이미 존재하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.EMAIL_ALREADY_USED.getMessage());
    }

    @DisplayName("회원가입 시 잘못된 이메일 형식인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEmailFormatIsInvalid() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setEmail("invalid-email");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 8자리 이상인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsLessThan8Characters() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword("short");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_TOO_SHORT.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 대문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainUppercaseLetter() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword("lowercase123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_UPPERCASE.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 소문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainLowercaseLetter() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword("UPPERCASE123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_LOWERCASE.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호에 특수문자가 포함되어 있는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordDoesNotContainSpecialCharacter() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword("Password123");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_MISSING_SPECIAL_CHAR.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 공백인지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword(" ");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_BLANK.getMessage());
    }

    @DisplayName("회원가입 시 비밀번호가 누락되었는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsMissing() {
        // given
        SignupRequestDto dto = createTestSignupRequestDto();
        dto.setPassword(null);

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> signupService.signup(dto));
        assertThat(exception.getMessage()).isEqualTo(SignupErrorCode.PASSWORD_BLANK.getMessage());
    }
}

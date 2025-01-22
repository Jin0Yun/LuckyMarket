package com.luckymarket.user.service.password;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.PasswordUpdateDto;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PasswordChangeServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordChangeService passwordChangeService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setPassword("oldPassword");
    }

    @DisplayName("비밀번호가 암호화되어 변경되는지 확인하는 테스트")
    @Test
    void shouldChangePasswordSuccessfully() {
        // given
        PasswordUpdateDto passwordDto = new PasswordUpdateDto();
        passwordDto.setPassword("newPassword123!");
        String encodedPassword = "encodedPassword123!";

        when(passwordEncoder.encode(passwordDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = passwordChangeService.changePassword(1L, passwordDto);

        // then
        assertEquals(encodedPassword, updatedMember.getPassword());
    }

    @DisplayName("비밀번호가 비어있는 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {
        // given
        PasswordUpdateDto passwordDto = new PasswordUpdateDto();
        passwordDto.setPassword("");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> passwordChangeService.changePassword(1L, passwordDto));
        assertEquals(SignupErrorCode.PASSWORD_BLANK.getMessage(), exception.getMessage());
    }

    @DisplayName("비밀번호 형식이 유효하지 않은 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        // given
        PasswordUpdateDto passwordDto = new PasswordUpdateDto();
        passwordDto.setPassword("short");

        // when & then
        SignupException exception = assertThrows(SignupException.class, () -> passwordChangeService.changePassword(1L, passwordDto));
        assertEquals(SignupErrorCode.PASSWORD_TOO_SHORT.getMessage(), exception.getMessage());
    }

    @DisplayName("존재하지 않는 사용자 ID로 비밀번호 변경 시 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        PasswordUpdateDto passwordDto = new PasswordUpdateDto();
        passwordDto.setPassword("newPassword123!");

        // when & then
        UserException exception = assertThrows(UserException.class, () -> passwordChangeService.changePassword(999L, passwordDto));
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }
}

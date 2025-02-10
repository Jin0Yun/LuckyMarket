package com.luckymarket.application.service.auth.impl;

import com.luckymarket.application.validation.EmailValidationRule;
import com.luckymarket.application.validation.LoginPasswordValidationRule;
import com.luckymarket.application.validation.PasswordValidationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class AuthValidationServiceImplTest {
    @Mock
    private EmailValidationRule emailValidationRule;

    @Mock
    private PasswordValidationRule passwordValidationRule;

    @Mock
    private LoginPasswordValidationRule loginPasswordValidationRule;

    @InjectMocks
    private AuthValidationServiceImpl authValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유효한 이메일을 검증할 때 validateEmail이 호출된다.")
    @Test
    void validateEmail_ShouldCallEmailValidationRule_WhenEmailIsValid() {
        // given
        String email = "test@example.com";

        // when
        authValidationService.validateEmail(email);

        // then
        verify(emailValidationRule, times(1)).validate(email);
    }

    @DisplayName("유효한 비밀번호를 검증할 때 validatePassword가 호출된다.")
    @Test
    void validatePassword_ShouldCallPasswordValidationRule_WhenPasswordIsValid() {
        // given
        String password = "validPassword123";

        // when
        authValidationService.validatePassword(password);

        // then
        verify(passwordValidationRule, times(1)).validate(password);
    }

    @DisplayName("로그인 비밀번호를 검증할 때 validateLoginPassword가 호출된다.")
    @Test
    void validateLoginPassword_ShouldCallLoginPasswordValidationRule_WhenPasswordIsValid() {
        // given
        String password = "validLoginPassword123";

        // when
        authValidationService.validateLoginPassword(password);

        // then
        verify(loginPasswordValidationRule, times(1)).validate(password);
    }
}

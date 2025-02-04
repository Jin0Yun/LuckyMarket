package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.user.usecase.validator.*;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MemberValidationServiceImplTest {
    @Mock
    private UserValidationRule userValidationRule;

    @Mock
    private PhoneNumberValidationRule phoneNumberValidationRule;

    @Mock
    private EmailValidationRule emailValidationRule;

    @Mock
    private PasswordValidationRule passwordValidationRule;

    @Mock
    private AddressValidationRule addressValidationRule;

    @InjectMocks
    private MemberValidationServiceImpl memberValidationServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("유효한 사용자 ID를 검증하면, 예외가 발생하지 않는다.")
    @Test
    void shouldNotThrowException_WhenUserIdIsValid() {
        // given
        Long validUserId = 1L;
        doNothing().when(userValidationRule).validate(validUserId);

        // when
        memberValidationServiceImpl.validateUser(validUserId);

        // then
        verify(userValidationRule, times(1)).validate(validUserId);
    }

    @DisplayName("유효하지 않은 사용자 ID를 검증하면, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenUserIdIsInvalid() {
        // given
        Long invalidUserId = 999L;
        doThrow(new UserException(UserErrorCode.UNAUTHORIZED_ACCESS))
                .when(userValidationRule).validate(invalidUserId);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validateUser(invalidUserId));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @DisplayName("유효한 이메일을 검증하면, 예외가 발생하지 않는다.")
    @Test
    void shouldNotThrowException_WhenEmailIsValid() {
        // given
        String validEmail = "test@example.com";
        doNothing().when(emailValidationRule).validate(validEmail);

        // when
        memberValidationServiceImpl.validateEmail(validEmail);

        // then
        verify(emailValidationRule, times(1)).validate(validEmail);
    }

    @DisplayName("유효하지 않은 이메일을 검증하면, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenEmailIsInvalid() {
        // given
        String invalidEmail = "invalid-email";
        doThrow(new UserException(UserErrorCode.INVALID_EMAIL_FORMAT))
                .when(emailValidationRule).validate(invalidEmail);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validateEmail(invalidEmail));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.INVALID_EMAIL_FORMAT.getMessage());
    }

    @DisplayName("유효한 비밀번호를 검증하면, 예외가 발생하지 않는다.")
    @Test
    void shouldNotThrowException_WhenPasswordIsValid() {
        // given
        String validPassword = "ValidPassword123!";
        doNothing().when(passwordValidationRule).validate(validPassword);

        // when
        memberValidationServiceImpl.validatePassword(validPassword);

        // then
        verify(passwordValidationRule, times(1)).validate(validPassword);
    }

    @DisplayName("비밀번호가 8자리 미만인 경우, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenPasswordIsTooShort() {
        // given
        String invalidPassword = "short";
        doThrow(new UserException(UserErrorCode.PASSWORD_TOO_SHORT))
                .when(passwordValidationRule).validate(invalidPassword);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePassword(invalidPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_TOO_SHORT.getMessage());
    }

    @DisplayName("비밀번호에 특수문자가 없는 경우, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenPasswordMissingSpecialChar() {
        // given
        String invalidPassword = "Password123";
        doThrow(new UserException(UserErrorCode.PASSWORD_MISSING_SPECIAL_CHAR))
                .when(passwordValidationRule).validate(invalidPassword);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePassword(invalidPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_SPECIAL_CHAR.getMessage());
    }

    @DisplayName("비밀번호에 대문자가 없는 경우, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenPasswordMissingUppercase() {
        // given
        String invalidPassword = "password123!";
        doThrow(new UserException(UserErrorCode.PASSWORD_MISSING_UPPERCASE))
                .when(passwordValidationRule).validate(invalidPassword);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePassword(invalidPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_UPPERCASE.getMessage());
    }

    @DisplayName("비밀번호에 소문자가 없는 경우, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenPasswordMissingLowercase() {
        // given
        String invalidPassword = "PASSWORD123!";
        doThrow(new UserException(UserErrorCode.PASSWORD_MISSING_LOWERCASE))
                .when(passwordValidationRule).validate(invalidPassword);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePassword(invalidPassword));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PASSWORD_MISSING_LOWERCASE.getMessage());
    }

    @DisplayName("유효한 전화번호를 검증하면, 예외가 발생하지 않는다.")
    @Test
    void shouldNotThrowException_WhenPhoneNumberIsValid() {
        // given
        String validPhoneNumber = "01012345678";
        doNothing().when(phoneNumberValidationRule).validate(validPhoneNumber);

        // when
        memberValidationServiceImpl.validatePhoneNumber(validPhoneNumber);

        // then
        verify(phoneNumberValidationRule, times(1)).validate(validPhoneNumber);
    }

    @DisplayName("유효하지 않은 전화번호를 검증하면, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenPhoneNumberIsInvalid() {
        // given
        String invalidPhoneNumber = "1234";
        doThrow(new UserException(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT))
                .when(phoneNumberValidationRule).validate(invalidPhoneNumber);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePhoneNumber(invalidPhoneNumber));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT.getMessage());
    }

    @DisplayName("전화번호가 null인 경우, 예외가 발생한다.")
    @Test
    void shouldNotValidate_WhenPhoneNumberIsNull() {
        // given
        String invalidPhoneNumber = "";
        doThrow(new UserException(UserErrorCode.PHONE_NUMBER_BLANK))
                .when(phoneNumberValidationRule).validate(invalidPhoneNumber);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validatePhoneNumber(invalidPhoneNumber));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PHONE_NUMBER_BLANK.getMessage());
    }

    @DisplayName("유효한 주소를 검증하면, 예외가 발생하지 않는다.")
    @Test
    void shouldNotThrowException_WhenAddressIsValid() {
        // given
        String validAddress = "서울시 강남구";
        doNothing().when(addressValidationRule).validate(validAddress);

        // when
        memberValidationServiceImpl.validateAddress(validAddress);

        // then
        verify(addressValidationRule, times(1)).validate(validAddress);
    }

    @DisplayName("주소가 null인 경우, 예외가 발생한다.")
    @Test
    void shouldThrowException_WhenAddressIsInvalid() {
        // given
        String invalidAddress = "";
        doThrow(new UserException(UserErrorCode.ADDRESS_BLANK))
                .when(addressValidationRule).validate(invalidAddress);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> memberValidationServiceImpl.validateAddress(invalidAddress));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.ADDRESS_BLANK.getMessage());
    }
}
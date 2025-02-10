package com.luckymarket.application.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PhoneNumberValidationRuleTest {
    private PhoneNumberValidationRule phoneNumberValidationRule;

    @BeforeEach
    void setUp() {
        phoneNumberValidationRule = new PhoneNumberValidationRule();
    }

    @DisplayName("전화번호가 빈 문자열일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPhoneNumberIsBlank() {
        // given
        String phoneNumber = "";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneNumberValidationRule.validate(phoneNumber));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.PHONE_NUMBER_BLANK.getMessage());
    }

    @DisplayName("전화번호가 잘못된 형식일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenPhoneNumberIsInvalidFormat() {
        // given
        String phoneNumber = "12345abc";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneNumberValidationRule.validate(phoneNumber));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT.getMessage());
    }

    @DisplayName("전화번호가 유효한 형식일 경우 예외가 발생하지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenPhoneNumberIsValid() {
        // given
        String phoneNumber = "+1234567890";

        // when & then
        assertDoesNotThrow(() -> phoneNumberValidationRule.validate(phoneNumber));
    }
}

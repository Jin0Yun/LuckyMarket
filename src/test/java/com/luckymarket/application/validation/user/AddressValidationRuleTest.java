package com.luckymarket.application.validation.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AddressValidationRuleTest {
    private AddressValidationRule addressValidationRule;

    @BeforeEach
    void setUp() {
        addressValidationRule = new AddressValidationRule();
    }

    @DisplayName("주소가 빈 문자열일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenAddressIsBlank() {
        // given
        String address = "";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> addressValidationRule.validate(address));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.ADDRESS_BLANK.getMessage());
    }

    @DisplayName("유효한 주소일 경우 예외가 발생하지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenAddressIsValid() {
        // given
        String address = "서울시 종로구";

        // when & then
        assertDoesNotThrow(() -> addressValidationRule.validate(address));
    }
}

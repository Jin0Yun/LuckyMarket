package com.luckymarket.application.service.user.impl;

import com.luckymarket.application.validation.AddressValidationRule;
import com.luckymarket.application.validation.PhoneNumberValidationRule;
import com.luckymarket.application.validation.UserValidationRule;
import com.luckymarket.application.validation.PasswordValidationRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class MemberValidationServiceImplTest {
    @Mock
    private UserValidationRule userValidationRule;

    @Mock
    private PhoneNumberValidationRule phoneNumberValidationRule;

    @Mock
    private PasswordValidationRule passwordValidationRule;

    @Mock
    private AddressValidationRule addressValidationRule;

    @InjectMocks
    private MemberValidationServiceImpl memberValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("사용자 ID를 검증할 때 유효한 ID가 들어오면 validateUser 호출")
    @Test
    void validateUser_ShouldCallUserValidationRule_WhenUserIdIsValid() {
        // given
        Long userId = 1L;

        // when
        memberValidationService.validateUser(userId);

        // then
        verify(userValidationRule, times(1)).validate(userId);
    }

    @DisplayName("비밀번호를 검증할 때 유효한 비밀번호가 들어오면 validatePassword 호출")
    @Test
    void validatePassword_ShouldCallPasswordValidationRule_WhenPasswordIsValid() {
        // given
        String password = "validPassword123";

        // when
        memberValidationService.validatePassword(password);

        // then
        verify(passwordValidationRule, times(1)).validate(password);
    }

    @DisplayName("전화번호를 검증할 때 유효한 전화번호가 들어오면 validatePhoneNumber 호출")
    @Test
    void validatePhoneNumber_ShouldCallPhoneNumberValidationRule_WhenPhoneNumberIsValid() {
        // given
        String phoneNumber = "+1234567890";

        // when
        memberValidationService.validatePhoneNumber(phoneNumber);

        // then
        verify(phoneNumberValidationRule, times(1)).validate(phoneNumber);
    }

    @DisplayName("주소를 검증할 때 유효한 주소가 들어오면 validateAddress 호출")
    @Test
    void validateAddress_ShouldCallAddressValidationRule_WhenAddressIsValid() {
        // given
        String address = "서울시 종로구";

        // when
        memberValidationService.validateAddress(address);

        // then
        verify(addressValidationRule, times(1)).validate(address);
    }
}
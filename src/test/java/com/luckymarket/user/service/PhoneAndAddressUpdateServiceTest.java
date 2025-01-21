package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class PhoneAndAddressUpdateServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PhoneAndAddressUpdateService phoneAndAddressUpdateService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setPhoneNumber("1234567890");
    }

    @DisplayName("전화번호 형식이 맞는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberFormatIsInvalid() {
        // given
        String invalidPhoneNumber = "invalidPhoneNumber";

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updatePhoneNumber(1L, invalidPhoneNumber));
        assertEquals(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 비어있는 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        // given
        String blankPhoneNumber = null;

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updatePhoneNumber(1L, blankPhoneNumber));
        assertEquals(UserErrorCode.PHONE_NUMBER_BLANK.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdatePhoneNumberSuccessfully() {
        // given
        String newPhoneNumber = "0987654321";
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneAndAddressUpdateService.updatePhoneNumber(1L, newPhoneNumber);

        // then
        assertEquals(newPhoneNumber, updatedMember.getPhoneNumber());
    }

    @DisplayName("주소가 비어있을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenAddressIsBlank() {
        // given
        String blankAddress = null;

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updateAddress(1L, blankAddress));
        assertEquals(UserErrorCode.ADDRESS_BLANK.getMessage(), exception.getMessage());
    }

    @DisplayName("주소가 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdateAddressSuccessfully() {
        // given
        String newAddress = "New Address";
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneAndAddressUpdateService.updateAddress(1L, newAddress);

        // then
        assertEquals(newAddress, updatedMember.getAddress());
    }
}

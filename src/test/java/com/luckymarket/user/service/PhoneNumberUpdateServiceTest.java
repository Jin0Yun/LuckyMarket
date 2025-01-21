package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
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

class PhoneNumberUpdateServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PhoneNumberUpdateService phoneNumberUpdateService;

    private Member member;
    private PhoneNumberUpdateDto phoneNumberUpdateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setPhoneNumber("1234567890");

        phoneNumberUpdateDto = new PhoneNumberUpdateDto();
        phoneNumberUpdateDto.setPhoneNumber("0987654321");
    }

    @DisplayName("전화번호 형식이 맞는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberFormatIsInvalid() {
        // given
        phoneNumberUpdateDto.setPhoneNumber("invalidPhoneNumber");

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneNumberUpdateService.updatePhoneNumber(1L, phoneNumberUpdateDto));
        assertEquals(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 비어있는 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        // given
        phoneNumberUpdateDto.setPhoneNumber(null);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneNumberUpdateService.updatePhoneNumber(1L, phoneNumberUpdateDto));
        assertEquals(UserErrorCode.PHONE_NUMBER_BLANK.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdatePhoneNumberSuccessfully() {
        // given
        phoneNumberUpdateDto.setPhoneNumber("0987654321");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneNumberUpdateService.updatePhoneNumber(1L, phoneNumberUpdateDto);

        // then
        assertEquals("0987654321", updatedMember.getPhoneNumber());
    }
}

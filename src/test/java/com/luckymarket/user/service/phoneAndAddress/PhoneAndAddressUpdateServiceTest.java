package com.luckymarket.user.service.phoneAndAddress;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.PhoneNumberAndAddressUpdateDto;
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
        PhoneNumberUpdateDto invalidPhoneDto = new PhoneNumberUpdateDto();
        invalidPhoneDto.setPhoneNumber("invalidPhoneNumber");

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updatePhoneNumber(1L, invalidPhoneDto));
        assertEquals(UserErrorCode.INVALID_PHONE_NUMBER_FORMAT.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 비어있는 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberIsBlank() {
        // given
        PhoneNumberUpdateDto blankPhoneDto = new PhoneNumberUpdateDto();
        blankPhoneDto.setPhoneNumber(null);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updatePhoneNumber(1L, blankPhoneDto));
        assertEquals(UserErrorCode.PHONE_NUMBER_BLANK.getMessage(), exception.getMessage());
    }

    @DisplayName("전화번호가 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdatePhoneNumberSuccessfully() {
        // given
        PhoneNumberUpdateDto newPhoneDto = new PhoneNumberUpdateDto();
        newPhoneDto.setPhoneNumber("0987654321");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneAndAddressUpdateService.updatePhoneNumber(1L, newPhoneDto);

        // then
        assertEquals("0987654321", updatedMember.getPhoneNumber());
    }

    @DisplayName("주소가 비어있을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenAddressIsBlank() {
        // given
        AddressUpdateDto blankAddressDto = new AddressUpdateDto();
        blankAddressDto.setAddress(null);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updateAddress(1L, blankAddressDto));
        assertEquals(UserErrorCode.ADDRESS_BLANK.getMessage(), exception.getMessage());
    }


    @DisplayName("주소가 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdateAddressSuccessfully() {
        // given
        AddressUpdateDto newAddressDto = new AddressUpdateDto();
        newAddressDto.setAddress("New Address");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneAndAddressUpdateService.updateAddress(1L, newAddressDto);

        // then
        assertEquals("New Address", updatedMember.getAddress());
    }

    @DisplayName("전화번호와 주소를 동시에 업데이트하는지 확인하는 테스트")
    @Test
    void shouldUpdatePhoneNumberAndAddressSuccessfully() {
        // given
        PhoneNumberAndAddressUpdateDto phoneAndAddressDto = new PhoneNumberAndAddressUpdateDto();
        phoneAndAddressDto.setPhoneNumber("0987654321");
        phoneAndAddressDto.setAddress("New Address");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = phoneAndAddressUpdateService.updatePhoneNumberAndAddress(1L, phoneAndAddressDto);

        // then
        assertEquals("0987654321", updatedMember.getPhoneNumber());
        assertEquals("New Address", updatedMember.getAddress());
    }

    @DisplayName("전화번호와 주소가 둘 다 비어있을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPhoneNumberAndAddressAreBlank() {
        // given
        PhoneNumberAndAddressUpdateDto blankPhoneAndAddressDto = new PhoneNumberAndAddressUpdateDto();
        blankPhoneAndAddressDto.setPhoneNumber(null);
        blankPhoneAndAddressDto.setAddress(null);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> phoneAndAddressUpdateService.updatePhoneNumberAndAddress(1L, blankPhoneAndAddressDto));
        assertEquals(UserErrorCode.PHONE_NUMBER_BLANK.getMessage(), exception.getMessage());
    }
}

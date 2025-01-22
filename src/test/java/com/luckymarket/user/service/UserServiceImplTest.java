package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.AddressUpdateDto;
import com.luckymarket.user.dto.NameUpdateDto;
import com.luckymarket.user.dto.PhoneNumberUpdateDto;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.account.AccountDeactivationService;
import com.luckymarket.user.service.password.PasswordChangeService;
import com.luckymarket.user.service.phoneAndAddress.PhoneAndAddressUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PhoneAndAddressUpdateService phoneAndAddressUpdateService;

    @Mock
    private PasswordChangeService passwordChangeService;

    @Mock
    private AccountDeactivationService accountDeactivationService;

    @InjectMocks
    private UserServiceImpl userService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setUsername("testuser");
        member.setPassword("oldPassword");
    }

    @DisplayName("이름 변경이 잘 되는지 확인하는 테스트")
    @Test
    void shouldUpdateNameSuccessfully() {
        // given
        NameUpdateDto nameUpdateDto = new NameUpdateDto();
        nameUpdateDto.setNewName("newName");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = userService.updateName(1L, nameUpdateDto);

        // then
        assertEquals("newName", updatedMember.getUsername());
        verify(userRepository, times(1)).save(member);
    }

    @DisplayName("전화번호 업데이트 서비스가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallPhoneAndAddressUpdateServiceWhenUpdatePhoneNumber() {
        // given
        PhoneNumberUpdateDto newPhoneDto = new PhoneNumberUpdateDto();
        newPhoneDto.setPhoneNumber("1234567890");

        // when
        userService.updatePhoneNumber(1L, newPhoneDto);

        // then
        verify(phoneAndAddressUpdateService, times(1)).updatePhoneNumber(1L, newPhoneDto);
    }

    @DisplayName("주소 업데이트 서비스가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallPhoneAndAddressUpdateServiceWhenUpdateAddress() {
        // given
        AddressUpdateDto newAddressDto = new AddressUpdateDto();
        newAddressDto.setAddress("newAddress");

        // when
        userService.updateAddress(1L, newAddressDto);

        // then
        verify(phoneAndAddressUpdateService, times(1)).updateAddress(1L, newAddressDto);
    }

    @DisplayName("전화번호와 주소 업데이트 서비스가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallPhoneAndAddressUpdateServiceWhenUpdatePhoneNumberAndAddress() {
        // given
        PhoneNumberUpdateDto newPhoneDto = new PhoneNumberUpdateDto();
        newPhoneDto.setPhoneNumber("1234567890");

        AddressUpdateDto newAddressDto = new AddressUpdateDto();
        newAddressDto.setAddress("newAddress");

        // when
        userService.updatePhoneNumberAndAddress(1L, newPhoneDto, newAddressDto);

        // then
        verify(phoneAndAddressUpdateService, times(1)).updatePhoneNumberAndAddress(1L, newPhoneDto, newAddressDto);
    }


    @DisplayName("비밀번호 변경 서비스가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallPasswordChangeServiceWhenChangePassword() {
        // given
        String newPassword = "newPassword123!";

        // when
        userService.changePassword(1L, newPassword);

        // then
        verify(passwordChangeService, times(1)).changePassword(1L, newPassword);
    }

    @DisplayName("회원 탈퇴 서비스가 호출되는지 확인하는 테스트")
    @Test
    void shouldCallAccountDeactivationServiceWhenDeleteAccount() {
        // given
        Long userId = 1L;

        // when
        userService.deleteAccount(userId);

        // then
        verify(accountDeactivationService, times(1)).deleteAccount(userId);
    }
}
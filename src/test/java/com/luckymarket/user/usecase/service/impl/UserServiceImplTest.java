package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.auth.exception.AuthErrorCode;
import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.service.RedisService;
import com.luckymarket.user.adapter.mapper.UserMapper;
import com.luckymarket.user.usecase.dto.*;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.MemberValidationService;
import com.luckymarket.user.usecase.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private MemberValidationService memberValidationService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RedisService redisService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Member member;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = createTestMember();
    }

    private Member createTestMember() {
        Member member = new Member();
        member.setId(userId);
        member.setUsername("testuser");
        member.setPassword("Password123!");
        member.setPhoneNumber("01012345678");
        member.setAddress("서울시 강남구");
        member.setStatus(Status.ACTIVE);
        return member;
    }

    @DisplayName("존재하는 회원 ID로 회원 정보를 조회하면, 해당 회원 정보를 반환한다.")
    @Test
    void shouldReturnMember_WhenUserExists() {
        // given
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        Member result = userService.getUserById(userId);

        // then
        assertThat(result).isEqualTo(member);
    }

    @DisplayName("존재하지 않는 회원 ID로 회원 정보를 조회하면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // given
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> userService.getUserById(userId));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 이름을 변경하면, 변경된 이름으로 저장된다.")
    @Test
    void shouldUpdateMemberName_WhenNameIsValid() {
        // given
        NameUpdateDto dto = new NameUpdateDto("newName");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        MemberResponseDto result = userService.updateName(userId, dto);

        // then
        assertThat(member.getUsername()).isEqualTo("newName");
    }

    @DisplayName("비밀번호를 변경하면, 변경된 비밀번호로 저장된다.")
    @Test
    void shouldUpdatePassword_WhenPasswordIsValid() {
        // given
        PasswordUpdateDto dto = new PasswordUpdateDto("newPassword");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));
        when(passwordService.encodePassword(dto.getPassword())).thenReturn("encodedNewPassword");

        // when
        userService.changePassword(userId, dto);

        // then
        assertThat(member.getPassword()).isEqualTo("encodedNewPassword");
    }

    @DisplayName("회원 전화번호를 변경하면, 변경된 전화번호로 저장된다.")
    @Test
    void shouldUpdateMemberPhoneNumber_WhenPhoneNumberIsValid() {
        // given
        PhoneNumberUpdateDto dto = new PhoneNumberUpdateDto("9876543210");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        MemberResponseDto result = userService.updatePhoneNumber(userId, dto);

        // then
        assertThat(member.getPhoneNumber()).isEqualTo("9876543210");
    }

    @DisplayName("회원 주소를 변경하면, 변경된 주소로 저장된다.")
    @Test
    void shouldUpdateMemberAddress_WhenAddressIsValid() {
        // given
        AddressUpdateDto dto = new AddressUpdateDto("new address");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        MemberResponseDto result = userService.updateAddress(userId, dto);

        // then
        assertThat(member.getAddress()).isEqualTo("new address");
    }

    @DisplayName("전화번호와 주소 동시에 변경 성공")
    @Test
    void updatePhoneNumberAndAddress_ShouldUpdateBothPhoneNumberAndAddress_WhenValidDto() {
        // given
        PhoneNumberAndAddressUpdateDto dto = new PhoneNumberAndAddressUpdateDto("9876543210", "new address");
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        MemberResponseDto result = userService.updatePhoneNumberAndAddress(userId, dto);

        // then
        assertThat(member.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(member.getAddress()).isEqualTo("new address");
    }

    @DisplayName("회원 탈퇴 요청 시, 회원 상태가 'DELETED'로 변경된다.")
    @Test
    void shouldUpdateStatusToDeleted_WhenUserIsActive() {
        // given
        member.setStatus(Status.ACTIVE);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when
        userService.deleteAccount(userId);

        // then
        assertThat(member.getStatus()).isEqualTo(Status.DELETED);
    }

    @DisplayName("이미 탈퇴한 회원을 탈퇴 처리하려고 하면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserAlreadyDeletedException_WhenUserIsAlreadyDeleted() {
        // given
        member.setStatus(Status.DELETED);
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(member));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userService.deleteAccount(userId));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.USER_ALREADY_DELETED.getMessage());
    }
}
package com.luckymarket.user.service;

import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.dto.*;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.mapper.MemberMapper;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private MemberMapper memberMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // given
        member = new Member();
        member.setId(1L);
        member.setUsername("testuser");
        member.setPassword("Password123!");
        member.setPhoneNumber("01012345678");
        member.setAddress("서울시 강남구");
        member.setStatus(Status.ACTIVE);

        when(memberMapper.toMemberResponseDto(member)).thenReturn(
                new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getUsername(),
                        member.getPhoneNumber(),
                        member.getAddress()
                )
        );
    }

    @DisplayName("존재하는 회원 ID로 회원 정보를 조회하면, 해당 회원 정보를 반환한다.")
    @Test
    void shouldReturnMember_WhenUserExists() {
        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        Member foundMember = userService.getUserById(1L);

        // then
        assertThat(foundMember).isEqualTo(member);
    }

    @DisplayName("존재하지 않는 회원 ID로 회원 정보를 조회하면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // given
        Long userId = 999L;

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //  then
        UserException exception = assertThrows(UserException.class, () -> userService.getUserById(999L));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 이름을 변경하면, 변경된 이름으로 저장된다.")
    @Test
    void shouldUpdateMemberName_WhenNameIsValid() {
        // given
        NameUpdateDto dto = new NameUpdateDto();
        dto.setNewName("newName");

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);
        when(memberMapper.toMemberResponseDto(member)).thenReturn(
                new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        dto.getNewName(),
                        member.getPhoneNumber(),
                        member.getAddress()
                )
        );
        MemberResponseDto updatedMember = userService.updateName(1L, dto);

        // then
        assertThat(updatedMember.getUsername()).isEqualTo("newName");
    }

    @DisplayName("비밀번호를 변경하면, 변경된 비밀번호로 저장된다.")
    @Test
    void shouldUpdatePassword_WhenPasswordIsValid() {
        // given
        PasswordUpdateDto dto = new PasswordUpdateDto();
        dto.setPassword("NewPassword123!");

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(memberValidationService).validateSignupFields(member);
        when(passwordService.encodePassword(dto.getPassword())).thenReturn("EncodedNewPassword123!");
        when(userRepository.save(member)).thenReturn(member);
        userService.changePassword(1L, dto);

        // then
        assertThat(member.getPassword()).isEqualTo("EncodedNewPassword123!");
    }

    @DisplayName("회원 전화번호를 변경하면, 변경된 전화번호로 저장된다.")
    @Test
    void shouldUpdateMemberPhoneNumber_WhenPhoneNumberIsValid() {
        // given
        PhoneNumberUpdateDto dto = new PhoneNumberUpdateDto();
        dto.setPhoneNumber("01098765432");

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(memberValidationService).validatePhoneNumber(dto.getPhoneNumber());
        when(userRepository.save(member)).thenReturn(member);
        when(memberMapper.toMemberResponseDto(member)).thenReturn(
                new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getUsername(),
                        dto.getPhoneNumber(),
                        member.getAddress()
                )
        );
        MemberResponseDto updatedMember = userService.updatePhoneNumber(1L, dto);

        // then
        assertThat(updatedMember.getPhoneNumber()).isEqualTo("01098765432");
    }

    @DisplayName("회원 주소를 변경하면, 변경된 주소로 저장된다.")
    @Test
    void shouldUpdateMemberAddress_WhenAddressIsValid() {
        // given
        AddressUpdateDto dto = new AddressUpdateDto();
        dto.setAddress("서울시 강북구");

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        doNothing().when(memberValidationService).validateAddress(dto.getAddress());
        when(userRepository.save(member)).thenReturn(member);
        when(memberMapper.toMemberResponseDto(member)).thenReturn(
                new MemberResponseDto(
                        member.getId(),
                        member.getEmail(),
                        member.getUsername(),
                        member.getPhoneNumber(),
                        dto.getAddress()
                )
        );
        MemberResponseDto updatedMember = userService.updateAddress(1L, dto);

        // then
        assertThat(updatedMember.getAddress()).isEqualTo("서울시 강북구");
    }

    @DisplayName("회원 탈퇴 요청 시, 회원 상태가 'DELETED'로 변경된다.")
    @Test
    void shouldUpdateStatusToDeleted_WhenUserIsActive() {
        // given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));

        // when
        userService.deleteAccount(userId);

        // then
        assertThat(member.getStatus()).isEqualTo(Status.DELETED);
    }

    @DisplayName("이미 탈퇴한 회원을 탈퇴 처리하려고 하면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserAlreadyDeletedException_WhenUserIsAlreadyDeleted() {
        // given
        Long userId = 1L;
        member.setStatus(Status.DELETED);
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));

        // then
        UserException exception = assertThrows(UserException.class, () -> userService.deleteAccount(1L));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.USER_ALREADY_DELETED.getMessage());
    }
}
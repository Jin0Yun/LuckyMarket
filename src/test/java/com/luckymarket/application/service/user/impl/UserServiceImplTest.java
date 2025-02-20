package com.luckymarket.application.service.user.impl;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.dto.user.*;
import com.luckymarket.application.validation.participation.UserExistenceValidationRule;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.infrastructure.redis.RedisService;
import com.luckymarket.domain.mapper.UserMapper;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.entity.user.Status;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import com.luckymarket.adapter.out.persistence.user.UserRepository;
import com.luckymarket.application.service.user.MemberValidationService;
import com.luckymarket.application.service.user.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    @Mock
    private UserExistenceValidationRule userExistenceValidationRule;

    @Mock
    private ProductRepository productRepository;

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
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when
        Member result = userService.getUserById(userId);

        // then
        assertThat(result).isEqualTo(member);
    }

    @DisplayName("존재하지 않는 회원 ID로 회원 정보를 조회하면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // given
        when(userExistenceValidationRule.getEntity(userId)).thenThrow(new AuthException(AuthErrorCode.USER_NOT_FOUND));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> userService.getUserById(userId));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 이름을 변경하면, 변경된 이름으로 저장된다.")
    @Test
    void shouldUpdateMemberName_WhenNameIsValid() {
        // given
        UserNameUpdateRequest dto = new UserNameUpdateRequest("newName");
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when
        UserProfileResponse result = userService.updateName(userId, dto);

        // then
        assertThat(member.getUsername()).isEqualTo("newName");
    }

    @DisplayName("비밀번호를 변경하면, 변경된 비밀번호로 저장된다.")
    @Test
    void shouldUpdatePassword_WhenPasswordIsValid() {
        // given
        UserPasswordUpdateRequest dto = new UserPasswordUpdateRequest("newPassword");
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);
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
        UserPhoneUpdateRequest dto = new UserPhoneUpdateRequest("9876543210");
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when
        UserProfileResponse result = userService.updatePhoneNumber(userId, dto);

        // then
        assertThat(member.getPhoneNumber()).isEqualTo("9876543210");
    }

    @DisplayName("회원 주소를 변경하면, 변경된 주소로 저장된다.")
    @Test
    void shouldUpdateMemberAddress_WhenAddressIsValid() {
        // given
        UserAddressUpdateRequest dto = new UserAddressUpdateRequest("new address");
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when
        UserProfileResponse result = userService.updateAddress(userId, dto);

        // then
        assertThat(member.getAddress()).isEqualTo("new address");
    }

    @DisplayName("전화번호와 주소 동시에 변경 성공")
    @Test
    void updatePhoneNumberAndAddress_ShouldUpdateBothPhoneNumberAndAddress_WhenValidDto() {
        // given
        UserContactUpdateRequest dto = new UserContactUpdateRequest("9876543210", "new address");
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when
        UserProfileResponse result = userService.updatePhoneNumberAndAddress(userId, dto);

        // then
        assertThat(member.getPhoneNumber()).isEqualTo("9876543210");
        assertThat(member.getAddress()).isEqualTo("new address");
    }

    @DisplayName("회원 탈퇴 요청 시, 회원 상태가 'DELETED'로 변경된다.")
    @Test
    void shouldUpdateStatusToDeleted_WhenUserIsActive() {
        // given
        member.setStatus(Status.ACTIVE);
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

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
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userService.deleteAccount(userId));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.USER_ALREADY_DELETED.getMessage());
    }

    @DisplayName("유저가 생성한 상품이 없으면, 예외를 발생시킨다.")
    @Test
    void shouldThrowUserException_WhenUserHasNoCreatedProducts() {
        // given
        when(productRepository.findByMemberId(userId)).thenReturn(Collections.emptyList());

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userService.getCreatedProducts(userId));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.NO_CREATED_PRODUCTS_FOUND.getMessage());
    }
}
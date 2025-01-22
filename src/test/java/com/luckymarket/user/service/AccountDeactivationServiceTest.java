package com.luckymarket.user.service;

import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Status;
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

class AccountDeactivationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AccountDeactivationService accountDeactivationService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setStatus(Status.ACTIVE);
    }

    @DisplayName("존재하지 않는 사용자 ID로 탈퇴 시 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenUserNotFoundForDeactivation() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        UserException exception = assertThrows(UserException.class, () -> accountDeactivationService.deleteAccount(nonExistentUserId));
        assertEquals(UserErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @DisplayName("이미 탈퇴한 사용자로 탈퇴 시 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenUserAlreadyDeleted() {
        // given
        member.setStatus(Status.DELETED);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));

        // when & then
        UserException exception = assertThrows(UserException.class, () -> accountDeactivationService.deleteAccount(1L));
        assertEquals(UserErrorCode.USER_ALREADY_DELETED.getMessage(), exception.getMessage());
    }

    @DisplayName("사용자 탈퇴가 성공하는지 확인하는 테스트")
    @Test
    void shouldDeactivateUserSuccessfully() {
        // given
        member.setStatus(Status.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        accountDeactivationService.deleteAccount(1L);

        // then
        assertEquals(Status.DELETED, member.getStatus());
    }
}

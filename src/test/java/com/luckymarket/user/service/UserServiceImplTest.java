package com.luckymarket.user.service;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.NameUpdateDto;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Member member;
    private NameUpdateDto nameUpdateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setId(1L);
        member.setUsername("OldName");
        nameUpdateDto = new NameUpdateDto();
    }

    @DisplayName("회원 이름을 변경되는지 확인하는 테스트")
    @Test
    void shouldUpdateNameSuccessfully() {
        // given
        nameUpdateDto.setNewName("NewName");
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(member));
        when(userRepository.save(member)).thenReturn(member);

        // when
        Member updatedMember = userService.updateName(1L, nameUpdateDto);

        // then
        assertThat(updatedMember.getUsername()).isEqualTo("NewName");
    }

    @DisplayName("회원 이름 변경 시 이름이 누락되었으면 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenNameIsMissing() {
        // given
        nameUpdateDto.setNewName(null);

        // when & then
        UserException exception = assertThrows(UserException.class, () -> userService.updateName(1L, nameUpdateDto));
        assertThat(exception.getMessage()).isEqualTo(UserErrorCode.NAME_BLANK.getMessage());
    }
}

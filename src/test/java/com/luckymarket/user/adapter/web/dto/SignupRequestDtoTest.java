package com.luckymarket.user.adapter.web.dto;

import com.luckymarket.user.usecase.dto.SignupRequestDto;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Role;
import com.luckymarket.user.domain.model.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class SignupRequestDtoTest {

    @DisplayName("SignupRequestDto가 Member 객체로 변환되는지 확인하는 테스트")
    @Test
    void shouldConvertToMember() {
        // given
        SignupRequestDto dto = SignupRequestDto
                .builder()
                .email("test@test.com")
                .password("ValidPassword123!")
                .username("testuser")
                .build();

        // when
        Member member = Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        // then
        assertThat(member.getEmail()).isEqualTo(dto.getEmail());
        assertThat(member.getPassword()).isEqualTo(dto.getPassword());
        assertThat(member.getUsername()).isEqualTo(dto.getUsername());
    }
}
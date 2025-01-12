package com.luckymarket.user.dto;

import com.luckymarket.user.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        Member member = dto.toEntity(dto.getPassword());

        // then
        assertThat(member.getEmail()).isEqualTo(dto.getEmail());
        assertThat(member.getPassword()).isEqualTo(dto.getPassword());
        assertThat(member.getUsername()).isEqualTo(dto.getUsername());
    }
}
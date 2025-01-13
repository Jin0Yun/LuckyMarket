package com.luckymarket.user.mapper;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.dto.SignupRequestDto;

public class MemberMapper {
    public static Member toEntity(SignupRequestDto dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }
}

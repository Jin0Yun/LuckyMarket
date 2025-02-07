package com.luckymarket.auth;

import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Role;
import com.luckymarket.user.domain.model.Status;
import org.springframework.stereotype.Component;

@Component
public class SignupMapper {
    public Member toEntity(SignupRequestDto dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }
}
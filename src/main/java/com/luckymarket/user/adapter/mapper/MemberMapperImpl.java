package com.luckymarket.user.adapter.mapper;

import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Role;
import com.luckymarket.user.domain.model.Status;
import com.luckymarket.user.usecase.dto.MemberResponseDto;
import com.luckymarket.user.usecase.dto.SignupRequestDto;
import org.springframework.stereotype.Component;

@Component
public class MemberMapperImpl implements MemberMapper {
    @Override
    public Member toEntity(SignupRequestDto dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .username(dto.getUsername())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @Override
    public MemberResponseDto toMemberResponseDto(Member member) {
        return new MemberResponseDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getPhoneNumber(),
                member.getAddress()
        );
    }
}

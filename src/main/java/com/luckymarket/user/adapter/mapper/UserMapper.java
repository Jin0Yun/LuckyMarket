package com.luckymarket.user.adapter.mapper;

import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.usecase.dto.MemberResponseDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
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

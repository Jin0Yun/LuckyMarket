package com.luckymarket.domain.mapper;

import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.application.dto.user.MemberResponseDto;
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

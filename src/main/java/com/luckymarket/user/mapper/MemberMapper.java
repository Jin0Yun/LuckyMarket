package com.luckymarket.user.mapper;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.MemberResponseDto;
import com.luckymarket.user.dto.SignupRequestDto;

public interface MemberMapper {
    MemberResponseDto toMemberResponseDto(Member member);
    Member toEntity(SignupRequestDto dto);
}
package com.luckymarket.user.adapter.mapper;

import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.usecase.dto.MemberResponseDto;
import com.luckymarket.auth.dto.SignupRequestDto;

public interface MemberMapper {
    MemberResponseDto toMemberResponseDto(Member member);
    Member toEntity(SignupRequestDto dto);
}
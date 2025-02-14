package com.luckymarket.domain.mapper;

import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.application.dto.user.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserProfileResponse toMemberResponseDto(Member member) {
        return new UserProfileResponse(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getPhoneNumber(),
                member.getAddress()
        );
    }
}

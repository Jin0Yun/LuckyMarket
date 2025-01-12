package com.luckymarket.user.dto;

import com.luckymarket.user.domain.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String email;
    private String password;
    private String username;

    public Member toEntity(String password) {
        return Member.builder().build();
    }
}
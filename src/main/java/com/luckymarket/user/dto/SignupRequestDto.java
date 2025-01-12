package com.luckymarket.user.dto;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String email;
    private String password;
    private String username;

    public Member toEntity(String password) {
        return Member
                .builder()
                .email(this.email)
                .password(password)
                .username(this.username)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }
}
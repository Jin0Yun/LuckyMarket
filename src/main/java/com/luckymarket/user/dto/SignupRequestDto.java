package com.luckymarket.user.dto;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDto {
    private String email;
    private String password;
    private String username;

    public Member toEntity(String password) {
        return Member
                .builder()
                .email(this.email)
                .password(this.password)
                .username(this.username)
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }
}
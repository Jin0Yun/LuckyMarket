package com.luckymarket.user.dto;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignupRequestDto {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
    @NotBlank(message = "이름은 필수 입력값입니다.")
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
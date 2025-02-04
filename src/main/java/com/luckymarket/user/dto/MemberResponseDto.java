package com.luckymarket.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원 정보 반환 DTO")
public class MemberResponseDto {
    private Long id;
    private String email;
    private String username;
    private String phoneNumber;
    private String address;
}

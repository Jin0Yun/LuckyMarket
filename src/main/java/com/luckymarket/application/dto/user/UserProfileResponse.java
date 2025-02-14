package com.luckymarket.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "회원 정보 반환 DTO")
public class UserProfileResponse {
    @Schema(description = "회원 ID", example = "1")
    private Long id;

    @Schema(description = "회원 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "회원 사용자 이름", example = "홍길동")
    private String username;

    @Schema(description = "회원 전화번호 (국제 형식)", example = "+821012345678")
    private String phoneNumber;

    @Schema(description = "회원 주소", example = "서울특별시 종로구 세종대로 1")
    private String address;
}

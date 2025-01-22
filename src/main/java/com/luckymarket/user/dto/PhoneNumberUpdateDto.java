package com.luckymarket.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원의 전화번호 업데이트를 위한 DTO")
public class PhoneNumberUpdateDto {
    @Schema(description = "회원의 전화번호", example = "+821012345678", required = true)
    private String phoneNumber;
}

package com.luckymarket.user.usecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원의 전화번호 및 주소 업데이트를 위한 DTO")
public class PhoneNumberAndAddressUpdateDto {
    @Schema(description = "회원의 전화번호", example = "+821012345678", required = true)
    private String phoneNumber;

    @Schema(description = "회원의 주소", example = "서울시 종로구", required = true)
    private String address;
}

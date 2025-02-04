package com.luckymarket.user.usecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원의 주소 업데이트를 위한 DTO")
public class AddressUpdateDto {
    @Schema(description = "회원의 새로운 주소", example = "서울시 종로구 광화문로 1", required = true)
    private String address;
}

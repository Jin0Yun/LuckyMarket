package com.luckymarket.user.usecase.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "회원의 이름 업데이트를 위한 DTO")
public class NameUpdateDto {
    @Schema(description = "회원의 새로운 이름", example = "홍길동", required = true)
    private String newName;
}

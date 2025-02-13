package com.luckymarket.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "회원 이름 업데이트 요청 DTO")
public class UserNameUpdateRequest {
    @Schema(description = "회원의 새로운 이름", example = "홍길동", required = true)
    private String newName;
}

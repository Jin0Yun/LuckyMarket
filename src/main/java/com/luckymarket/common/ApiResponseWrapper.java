package com.luckymarket.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공통 API 응답 모델")
public class ApiResponseWrapper<T> {
    @Schema(description = "응답 메시지")
    private String message;
    @Schema(description = "HTTP 상태 코드")
    private int status;
    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponseWrapper<T> success(String message, T data){
        return new ApiResponseWrapper<>(message, 200, data);
    }

    public static <T> ApiResponseWrapper<T> error(String message, int status){
        return new ApiResponseWrapper<>(message, status, null);
    }

    public static <T> ApiResponseWrapper<T> withData(String message, T data){
        return new ApiResponseWrapper<>(message, 200, data);
    }
}

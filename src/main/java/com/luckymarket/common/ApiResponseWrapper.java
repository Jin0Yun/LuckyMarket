package com.luckymarket.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "API 응답의 Wrapper 클래스")
public class ApiResponseWrapper<T> {
    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code;

    @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponseWrapper<T> success(String message, T data) {
        return new ApiResponseWrapper<>("SUCCESS", message, HttpStatus.OK.value(), data);
    }

    public static <T> ApiResponseWrapper<T> error(String message, int status) {
        return new ApiResponseWrapper<>("ERROR", message, status, null);
    }

    public static <T> ApiResponseWrapper<T> withData(String message, T data) {
        return new ApiResponseWrapper<>("SUCCESS", message, HttpStatus.OK.value(), data);
    }
}
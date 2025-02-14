package com.luckymarket.application.dto;

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
@Schema(description = "공통 API 응답 형식")
public class ApiResponse<T> {
    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code;

    @Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int status;

    @Schema(description = "응답 데이터")
    private T data;

    public static <T> ApiResponse<T> createResponse(String code, String message, HttpStatus status, T data) {
        return new ApiResponse<>(code, message, status.value(), data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return createResponse("SUCCESS", message, HttpStatus.OK, data);
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        return createResponse("ERROR", message, HttpStatus.valueOf(status), null);
    }
}

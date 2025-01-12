package com.luckymarket.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data){
        return new ApiResponse<>(message, 200, data);
    }

    public static <T> ApiResponse<T> error(String message, int status){
        return new ApiResponse<>(message, status, null);
    }

    public static <T> ApiResponse<T> withData(String message, T data){
        return new ApiResponse<>(message, 200, data);
    }
}

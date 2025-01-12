package com.luckymarket.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseWrapper<T> {
    private String message;
    private int status;
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

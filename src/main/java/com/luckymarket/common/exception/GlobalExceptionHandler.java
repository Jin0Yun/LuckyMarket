package com.luckymarket.common.exception;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.user.domain.exception.UserException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {
    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseWrapper<String> handleUserException(UserException e) {
        return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseWrapper<String> handleException(Exception e) {
        return ApiResponseWrapper.error("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
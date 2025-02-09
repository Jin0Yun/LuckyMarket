package com.luckymarket.common.exception;

import com.luckymarket.auth.exception.AuthException;
import com.luckymarket.auth.exception.RedisException;
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
    public ApiResponseWrapper<Object> handleUserException(UserException e) {
        return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponseWrapper<Object> handleAuthException(AuthException ex) {
        return ApiResponseWrapper.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(RedisException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponseWrapper<Object> handleRedisException(RedisException ex) {
        return ApiResponseWrapper.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponseWrapper<Object> handleException(Exception e) {
        return ApiResponseWrapper.error("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
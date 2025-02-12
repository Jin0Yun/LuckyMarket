package com.luckymarket.adapter.in.web;

import com.luckymarket.application.dto.ApiResponse;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.exception.auth.RedisException;
import com.luckymarket.domain.exception.product.CategoryException;
import com.luckymarket.domain.exception.product.ProductException;
import com.luckymarket.domain.exception.user.UserException;
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
    public ApiResponse<Object> handleUserException(UserException e) {
        return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Object> handleAuthException(AuthException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(RedisException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleRedisException(RedisException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @ExceptionHandler(CategoryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleCategoryException(CategoryException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(ProductException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleProductException(ProductException ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleException(Exception e) {
        return ApiResponse.error("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
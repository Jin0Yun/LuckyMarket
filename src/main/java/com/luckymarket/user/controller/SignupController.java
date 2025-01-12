package com.luckymarket.user.controller;

import com.luckymarket.common.ApiResponse;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.service.SignupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/member")
public class SignupController {
    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequestDto signupRequestDto) {
        log.debug("회원가입 요청을 받았습니다. 이메일: {}", signupRequestDto.getEmail());
        try {
            signupService.signup(signupRequestDto);
            log.info("회원가입이 성공적으로 완료되었습니다");
            ApiResponse<String> response = ApiResponse.success("회원가입 성공", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SignupException e) {
            log.error("회원가입에 실패했습니다: {}", e.getMessage());
            ApiResponse<String> response = ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("예기치 못한 오류 발생", e);
            ApiResponse<String> response = ApiResponse.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
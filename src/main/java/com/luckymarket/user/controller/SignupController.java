package com.luckymarket.user.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "회원 API", description = "회원 관련 API")
public class SignupController {
    private final SignupService signupService;

    @Autowired
    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "사용자가 이메일, 비밀번호, 이름으로 회원가입을 요청합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청(유효성 검증 실패)"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<ApiResponseWrapper<String>> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        log.debug("회원가입 요청을 받았습니다. 이메일: {}", signupRequestDto.getEmail());
        try {
            signupService.signup(signupRequestDto);
            log.info("회원가입이 성공적으로 완료되었습니다");
            ApiResponseWrapper<String> response = ApiResponseWrapper.success("회원가입 성공", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (SignupException e) {
            log.error("회원가입에 실패했습니다: {}", e.getMessage());
            ApiResponseWrapper<String> response = ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("예기치 못한 오류 발생", e);
            ApiResponseWrapper<String> response = ApiResponseWrapper.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
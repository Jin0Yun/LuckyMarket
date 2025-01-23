package com.luckymarket.user.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.*;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
@Tag(name = "회원 API", description = "회원 정보 수정 및 관리 관련 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/get-user/{userId}")
    @Operation(
            summary = "회원 정보 조회",
            description = "회원의 이름, 전화번호, 주소 등을 조회합니다."
    )
    public ApiResponseWrapper<Member> getUser(@PathVariable Long userId) {
        try {
            Member member = userService.getUserById(userId);
            return ApiResponseWrapper.success("회원 정보를 성공적으로 조회했습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PatchMapping("/update-password/{userId}")
    @Operation(
            summary = "비밀번호 변경",
            description = "회원의 비밀번호를 변경합니다."
    )
    public ApiResponseWrapper<Member> changePassword(@PathVariable Long userId, @RequestBody PasswordUpdateDto dto) {
        try {
            Member member = userService.changePassword(userId, dto);
            return ApiResponseWrapper.success("비밀번호가 성공적으로 변경되었습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PatchMapping("/update-phone/{userId}")
    @Operation(
            summary = "전화번호 변경",
            description = "회원의 전화번호를 변경합니다."
    )
    public ApiResponseWrapper<Member> updatePhoneNumber(@PathVariable Long userId, @RequestBody PhoneNumberUpdateDto dto) {
        try {
            Member member = userService.updatePhoneNumber(userId, dto);
            return ApiResponseWrapper.success("전화번호가 성공적으로 변경되었습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PatchMapping("/update-address/{userId}")
    @Operation(
            summary = "주소 변경",
            description = "회원의 주소를 변경합니다."
    )
    public ApiResponseWrapper<Member> updateAddress(@PathVariable Long userId, @RequestBody AddressUpdateDto dto) {
        try {
            Member member = userService.updateAddress(userId, dto);
            return ApiResponseWrapper.success("주소가 성공적으로 변경되었습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PatchMapping("/phone-address/{userId}")
    @Operation(
            summary = "전화번호 및 주소 변경",
            description = "회원의 전화번호와 주소를 변경합니다."
    )
    public ApiResponseWrapper<Member> updatePhoneNumberAndAddress(
            @PathVariable Long userId,
            @RequestBody PhoneNumberAndAddressUpdateDto phoneAndAddressDto
    ) {
        try {
            Member member = userService.updatePhoneNumberAndAddress(userId, phoneAndAddressDto);
            return ApiResponseWrapper.success("전화번호와 주소가 성공적으로 업데이트되었습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PatchMapping("/update-name/{userId}")
    @Operation(
            summary = "이름 변경",
            description = "회원의 이름을 변경합니다."
    )
    public ApiResponseWrapper<Member> updateName(@PathVariable Long userId, @RequestBody NameUpdateDto nameUpdateDto) {
        try {
            Member member = userService.updateName(userId, nameUpdateDto);
            return ApiResponseWrapper.success("이름이 성공적으로 변경되었습니다", member);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @DeleteMapping("/delete-account/{userId}")
    @Operation(
            summary = "회원 탈퇴",
            description = "회원 계정을 삭제합니다."
    )
    public ApiResponseWrapper<Void> deleteAccount(@PathVariable Long userId) {
        try {
            userService.deleteAccount(userId);
            return ApiResponseWrapper.success("회원 탈퇴가 성공적으로 처리되었습니다", null);
        } catch (UserException e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}

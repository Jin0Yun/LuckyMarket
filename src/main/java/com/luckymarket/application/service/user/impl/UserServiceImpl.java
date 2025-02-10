package com.luckymarket.application.service.user.impl;

import com.luckymarket.adapter.out.persistence.user.UserRepository;
import com.luckymarket.application.dto.user.*;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.infrastructure.redis.RedisService;
import com.luckymarket.domain.mapper.UserMapper;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.entity.user.Status;
import com.luckymarket.domain.exception.user.UserErrorCode;
import com.luckymarket.domain.exception.user.UserException;
import com.luckymarket.application.service.user.MemberValidationService;
import com.luckymarket.application.service.user.PasswordService;
import com.luckymarket.application.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordService passwordService;
    private final MemberValidationService memberValidationService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Member getUserById(Long userId) {
        memberValidationService.validateUser(userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponseDto getUser(Long userId) {
        Member member = getUserById(userId);
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updateName(Long userId, NameUpdateDto dto) {
        Member member = getUserById(userId);
        member.setUsername(dto.getNewName());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updatePhoneNumber(Long userId, PhoneNumberUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updateAddress(Long userId, AddressUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        member.setAddress(dto.getAddress());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public MemberResponseDto updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setAddress(dto.getAddress());
        return userMapper.toMemberResponseDto(member);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validatePassword(dto.getPassword());
        String encodedPassword = passwordService.encodePassword(dto.getPassword());
        member.setPassword(encodedPassword);
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId) {
        Member member = getUserById(userId);
        if (member.getStatus() == Status.DELETED) {
            throw new UserException(UserErrorCode.USER_ALREADY_DELETED);
        }
        redisService.removeRefreshToken(userId);
        redisService.markUserAsLoggedOut(userId);
        member.setStatus(Status.DELETED);
    }
}
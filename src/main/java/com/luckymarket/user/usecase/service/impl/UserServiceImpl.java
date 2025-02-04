package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.usecase.dto.*;
import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.domain.model.Status;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import com.luckymarket.user.adapter.mapper.MemberMapper;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordService passwordService;
    private final MemberValidationService memberValidationService;
    private final MemberMapper memberMapper;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RedisService redisService,
            PasswordService passwordService,
            MemberValidationService memberValidationService,
            MemberMapper memberMapper
    ) {
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.passwordService = passwordService;
        this.memberValidationService = memberValidationService;
        this.memberMapper = memberMapper;
    }

    @Override
    public Member getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public MemberResponseDto getUser(Long userId) {
        Member member = getUserById(userId);
        return memberMapper.toMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto updateName(Long userId, NameUpdateDto nameDto) {
        Member member = getUserById(userId);
        member.setUsername(nameDto.getNewName());
        userRepository.save(member);
        return memberMapper.toMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        Member member = getUserById(userId);
        memberValidationService.validatePhoneNumber(phoneDto.getPhoneNumber());
        member.setPhoneNumber(phoneDto.getPhoneNumber());
        userRepository.save(member);
        return memberMapper.toMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto updateAddress(Long userId, AddressUpdateDto addressDto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(addressDto.getAddress());
        member.setAddress(addressDto.getAddress());
        userRepository.save(member);
        return memberMapper.toMemberResponseDto(member);
    }

    @Override
    public MemberResponseDto updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setAddress(dto.getAddress());
        userRepository.save(member);
        return memberMapper.toMemberResponseDto(member);
    }

    @Override
    public void changePassword(Long userId, PasswordUpdateDto passwordDto) {
        Member member = getUserById(userId);
        memberValidationService.validatePassword(member.getPassword());
        String encodedPassword = passwordService.encodePassword(passwordDto.getPassword());
        member.setPassword(encodedPassword);
        userRepository.save(member);
    }

    @Override
    public void deleteAccount(Long userId) {
        Member member = getUserById(userId);
        if (member.getStatus() == Status.DELETED) {
            throw new UserException(UserErrorCode.USER_ALREADY_DELETED);
        }
        redisService.deleteRefreshToken(userId);
        member.setStatus(Status.DELETED);
        userRepository.save(member);
    }
}
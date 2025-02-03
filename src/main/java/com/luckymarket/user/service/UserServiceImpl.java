package com.luckymarket.user.service;

import com.luckymarket.auth.service.redis.RedisService;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.dto.*;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordService passwordService;
    private final MemberValidationService memberValidationService;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            RedisService redisService,
            PasswordService passwordService,
            MemberValidationService memberValidationService
    ) {
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.passwordService = passwordService;
        this.memberValidationService = memberValidationService;
    }

    @Override
    public Member getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Member updateName(Long userId, NameUpdateDto nameDto) {
        Member member = getUserById(userId);
        member.setUsername(nameDto.getNewName());
        return userRepository.save(member);
    }

    @Override
    public Member updatePhoneNumber(Long userId, PhoneNumberUpdateDto phoneDto) {
        Member member = getUserById(userId);
        memberValidationService.validatePhoneNumber(phoneDto.getPhoneNumber());
        member.setPhoneNumber(phoneDto.getPhoneNumber());
        return userRepository.save(member);
    }

    @Override
    public Member updateAddress(Long userId, AddressUpdateDto addressDto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(addressDto.getAddress());
        member.setAddress(addressDto.getAddress());
        return userRepository.save(member);
    }

    @Override
    public Member updatePhoneNumberAndAddress(Long userId, PhoneNumberAndAddressUpdateDto dto) {
        Member member = getUserById(userId);
        memberValidationService.validateAddress(dto.getAddress());
        memberValidationService.validatePhoneNumber(dto.getPhoneNumber());
        member.setPhoneNumber(dto.getPhoneNumber());
        member.setAddress(dto.getAddress());
        return userRepository.save(member);
    }

    @Override
    public Member changePassword(Long userId, PasswordUpdateDto passwordDto) {
        Member member = getUserById(userId);
        memberValidationService.validateSignupFields(member);
        String encodedPassword = passwordService.encodePassword(passwordDto.getPassword());
        member.setPassword(encodedPassword);
        return userRepository.save(member);
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
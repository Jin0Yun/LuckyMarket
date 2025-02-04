package com.luckymarket.user.usecase.service.impl;

import com.luckymarket.user.domain.model.Member;

import com.luckymarket.user.usecase.dto.SignupRequestDto;
import com.luckymarket.user.adapter.mapper.MemberMapper;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final MemberValidationService memberValidationService;
    private final PasswordService passwordService;
    private final MemberMapper memberMapper;

    @Autowired
    public SignupServiceImpl(
            UserRepository userRepository,
            MemberValidationService memberValidator,
            PasswordService passwordService,
            MemberMapper memberMapper
    ) {
        this.userRepository = userRepository;
        this.memberValidationService = memberValidator;
        this.passwordService = passwordService;
        this.memberMapper = memberMapper;
    }

    @Override
    public Member signup(SignupRequestDto signupRequestDto) {
        memberValidationService.validateSignupFields(signupRequestDto);
        String encodedPassword = passwordService.encodePassword(signupRequestDto.getPassword());

        SignupRequestDto encodedDto = SignupRequestDto.builder()
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .username(signupRequestDto.getUsername())
                .build();

        Member member = memberMapper.toEntity(encodedDto);

        return userRepository.save(member);
    }
}
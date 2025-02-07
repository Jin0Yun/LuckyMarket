package com.luckymarket.auth.service;

import com.luckymarket.auth.validator.AuthValidationService;
import com.luckymarket.user.domain.exception.UserErrorCode;
import com.luckymarket.user.domain.exception.UserException;
import com.luckymarket.user.domain.model.Member;

import com.luckymarket.auth.dto.SignupRequestDto;
import com.luckymarket.user.adapter.mapper.MemberMapper;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupServiceImpl implements SignupService {
    private final UserRepository userRepository;
    private final AuthValidationService authValidationService;
    private final PasswordService passwordService;
    private final MemberMapper memberMapper;

    @Autowired
    public SignupServiceImpl(
            UserRepository userRepository,
            AuthValidationService authValidationService,
            PasswordService passwordService,
            MemberMapper memberMapper
    ) {
        this.userRepository = userRepository;
        this.authValidationService = authValidationService;
        this.passwordService = passwordService;
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        authValidationService.validateEmail(signupRequestDto.getEmail());
        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_USED);
        }

        authValidationService.validatePassword(signupRequestDto.getPassword());

        signupRequestDto.setPassword(passwordService.encodePassword(signupRequestDto.getPassword()));
        Member member = memberMapper.toEntity(signupRequestDto);
        userRepository.save(member);
    }
}
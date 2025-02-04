package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;

import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.mapper.MemberMapper;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.MemberValidationService;
import com.luckymarket.user.service.PasswordService;
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
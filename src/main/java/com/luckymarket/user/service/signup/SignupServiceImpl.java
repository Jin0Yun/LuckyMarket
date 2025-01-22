package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.exception.SignupErrorCode;
import com.luckymarket.user.exception.SignupException;
import com.luckymarket.user.mapper.MemberMapper;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.validator.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class SignupServiceImpl implements SignupService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignupServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member signup(SignupRequestDto signupRequestDto) {
        log.debug("회원가입 요청을 받았습니다. 이메일: {}", signupRequestDto.getEmail());
        validateEmail(signupRequestDto.getEmail());
        PasswordValidator.validatePassword(signupRequestDto.getPassword());

        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        Member member = MemberMapper.toEntity(signupRequestDto);
        member.setPassword(password);
        userRepository.save(member);
        log.info("회원가입 성공. 이메일: {}", member.getEmail());
        return member;
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new SignupException(SignupErrorCode.EMAIL_BLANK);
        }

        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new SignupException(SignupErrorCode.INVALID_EMAIL_FORMAT);
        }
        if (userRepository.existsByEmail(email)) {
            throw new SignupException(SignupErrorCode.EMAIL_ALREADY_USED);
        }
    }
}
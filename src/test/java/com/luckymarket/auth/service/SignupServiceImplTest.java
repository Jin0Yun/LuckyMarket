package com.luckymarket.auth.service;

import com.luckymarket.user.domain.model.Member;
import com.luckymarket.user.usecase.dto.SignupRequestDto;
import com.luckymarket.user.adapter.mapper.MemberMapper;
import com.luckymarket.user.domain.repository.UserRepository;
import com.luckymarket.user.usecase.service.MemberValidationService;
import com.luckymarket.user.usecase.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class SignupServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private MemberValidationService memberValidationService;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private SignupServiceImpl signupService;

    private SignupRequestDto signupRequestDto;
    private final String encodedPassword = "encodedPassword123!";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        signupRequestDto = SignupRequestDto.builder()
                .email("test@test.com")
                .password("ValidPassword123!")
                .username("testUser")
                .build();
    }

    @DisplayName("정상적인 회원가입 요청 시, 회원 정보가 정상적으로 저장된다.")
    @Test
    void shouldSaveMember_WhenSignupIsSuccessful() {
        // Given
        doNothing().when(memberValidationService).validateEmail(signupRequestDto.getEmail());
        doNothing().when(memberValidationService).validatePassword(signupRequestDto.getPassword());
        when(passwordService.encodePassword(signupRequestDto.getPassword())).thenReturn(encodedPassword);
        when(memberMapper.toEntity(any(SignupRequestDto.class))).thenAnswer(invocation -> {
            SignupRequestDto dto = invocation.getArgument(0);
            return Member.builder()
                    .email(dto.getEmail())
                    .password(encodedPassword)
                    .username(dto.getUsername())
                    .build();
        });

        // When
        signupService.signup(signupRequestDto);

        // Then
        verify(userRepository).save(argThat(savedMember ->
                savedMember.getEmail().equals(signupRequestDto.getEmail()) &&
                savedMember.getUsername().equals(signupRequestDto.getUsername()) &&
                savedMember.getPassword().equals(encodedPassword)
        ));
    }
}
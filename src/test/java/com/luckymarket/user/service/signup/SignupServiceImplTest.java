package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.MemberValidationService;
import com.luckymarket.user.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SignupServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private MemberValidationService memberValidationService;

    @InjectMocks
    private SignupServiceImpl signupService;

    private SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        signupRequestDto = SignupRequestDto.builder()
                .email("test@test.com")
                .password("ValidPassword123!")
                .username("testUser")
                .build();
    }

    @DisplayName("정상적인 회원가입 요청 시, 회원가입이 성공적으로 완료된다.")
    @Test
    void shouldSignupSuccessfully_WhenRequestIsValid() {
        // Given
        when(passwordService.encodePassword(signupRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(memberValidationService).validateSignupFields(any(Member.class));

        // When
        Member member = signupService.signup(signupRequestDto);

        // Then
        assertNotNull(member);
        verify(userRepository, times(1)).save(any(Member.class));
    }

    @DisplayName("회원 정보가 정상적으로 저장된다.")
    @Test
    void shouldSaveMember_WhenSignupIsSuccessful() {
        // Given
        when(passwordService.encodePassword(signupRequestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Member member = signupService.signup(signupRequestDto);

        // Then
        assertEquals("test@test.com", member.getEmail());
        assertEquals("encodedPassword", member.getPassword());
        assertEquals("testUser", member.getUsername());
    }
}
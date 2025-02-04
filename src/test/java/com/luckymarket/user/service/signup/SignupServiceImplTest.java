package com.luckymarket.user.service.signup;

import com.luckymarket.user.domain.Member;
import com.luckymarket.user.domain.Role;
import com.luckymarket.user.domain.Status;
import com.luckymarket.user.dto.SignupRequestDto;
import com.luckymarket.user.mapper.MemberMapper;
import com.luckymarket.user.repository.UserRepository;
import com.luckymarket.user.service.MemberValidationService;
import com.luckymarket.user.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private Member member;
    private final String encodedPassword = "encodedPassword123!";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        signupRequestDto = SignupRequestDto.builder()
                .email("test@test.com")
                .password("ValidPassword123!")
                .username("testUser")
                .build();

        member = Member.builder()
                .email(signupRequestDto.getEmail())
                .password(encodedPassword)
                .username(signupRequestDto.getUsername())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();
    }

    @DisplayName("정상적인 회원가입 요청 시, 회원 정보가 정상적으로 저장된다.")
    @Test
    void shouldSaveMember_WhenSignupIsSuccessful() {
        // Given
        doNothing().when(memberValidationService).validateSignupFields(signupRequestDto);
        when(passwordService.encodePassword(signupRequestDto.getPassword())).thenReturn(encodedPassword);
        when(memberMapper.toEntity(any(SignupRequestDto.class))).thenReturn(member);
        when(userRepository.save(any(Member.class))).thenReturn(member);

        // When
        Member savedMember = signupService.signup(signupRequestDto);

        // Then
        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo(signupRequestDto.getEmail());
        assertThat(savedMember.getUsername()).isEqualTo(signupRequestDto.getUsername());
        assertThat(savedMember.getPassword()).isEqualTo(encodedPassword);
    }
}
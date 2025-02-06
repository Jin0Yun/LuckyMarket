package com.luckymarket.user.usecase.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class PasswordServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("비밀번호를 인코딩하면, 인코딩된 비밀번호를 반환한다.")
    @Test
    void shouldReturnEncodedPassword_WhenPasswordIsEncoded() {
        // given
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        String result = passwordService.encodePassword(rawPassword);

        // then
        assertThat(result).isEqualTo(encodedPassword);
        verify(passwordEncoder, times(1)).encode(rawPassword);
    }
}
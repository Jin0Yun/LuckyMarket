package com.luckymarket.application.service.participation.Impl;

import com.luckymarket.adapter.out.persistence.participation.ParticipationRepository;
import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.dto.participation.ParticipationRequest;
import com.luckymarket.application.validation.participation.*;
import com.luckymarket.domain.entity.participation.Participation;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.entity.user.Status;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.exception.persistence.ParticipationErrorCode;
import com.luckymarket.domain.exception.persistence.ParticipationException;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ParticipationServiceImplTest {
    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductExistenceValidationRule productExistenceValidationRule;

    @Mock
    private UserExistenceValidationRule userExistenceValidationRule;

    @Mock
    private ParticipationValidationRule participationValidationRule;

    @Mock
    private ParticipationExistenceValidationRule participationExistenceValidationRule;

    @InjectMocks
    private ParticipationServiceImpl participationService;

    private Member member;
    private Product product;
    private Long userId = 1L;
    private Long productId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = createTestMember();
        product = createTestProduct();
    }

    private Member createTestMember() {
        Member member = new Member();
        member.setId(userId);
        member.setUsername("testuser");
        member.setPassword("Password123!");
        member.setPhoneNumber("01012345678");
        member.setAddress("서울시 강남구");
        member.setStatus(Status.ACTIVE);
        return member;
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setId(productId);
        product.setTitle("신선한 사과");
        product.setDescription("100% 유기농 사과");
        product.setPrice(BigDecimal.valueOf(5000));
        product.setParticipants(0);
        product.setMaxParticipants(10);
        product.setStatus(ProductStatus.ONGOING);
        product.setEndDate(LocalDate.of(2025, 12, 31));
        return product;
    }

    @DisplayName("상품 참여 성공")
    @Test
    void shouldParticipateInProduct_WhenProductAndUserExist() {
        // given
        when(productExistenceValidationRule.getEntity(productId)).thenReturn(product);
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);
        when(participationRepository.save(any(Participation.class))).thenReturn(new Participation());

        // when
        participationService.participateInProduct(productId, userId);

        // then
        verify(participationRepository, times(1)).save(any(Participation.class));
        verify(productRepository, times(1)).save(product);
        assertThat(product.getParticipants()).isEqualTo(1);
    }

    @DisplayName("상품 참여 시 상품이 존재하지 않으면 예외 발생")
    @Test
    void shouldThrowException_WhenProductDoesNotExistForParticipation() {
        // given
        when(productExistenceValidationRule.getEntity(productId)).thenThrow(new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> participationService.participateInProduct(productId, userId));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("상품 참여 시 사용자가 존재하지 않으면 예외 발생")
    @Test
    void shouldThrowException_WhenUserDoesNotExistForParticipation() {
        // given
        when(userExistenceValidationRule.getEntity(userId)).thenThrow(new AuthException(AuthErrorCode.USER_NOT_FOUND));

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> participationService.participateInProduct(productId, userId));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("상품 참여 시 참여 조건이 유효하지 않으면 예외 발생")
    @Test
    void shouldThrowException_WhenParticipationRequestIsInvalid() {
        // given
        when(productExistenceValidationRule.getEntity(productId)).thenReturn(product);
        when(userExistenceValidationRule.getEntity(userId)).thenReturn(member);
        doThrow(new ParticipationException(ParticipationErrorCode.INVALID_PARTICIPATION)).when(participationValidationRule).validate(any(ParticipationRequest.class));

        // when & then
        ParticipationException exception = assertThrows(ParticipationException.class, () -> participationService.participateInProduct(productId, userId));
        assertThat(exception.getMessage()).isEqualTo(ParticipationErrorCode.INVALID_PARTICIPATION.getMessage());
    }

    @DisplayName("상품 참여 취소 성공")
    @Test
    void shouldLeaveProduct_WhenParticipationExists() {
        // given
        Participation participation = Participation.builder()
                .product(product)
                .member(member)
                .build();
        when(productExistenceValidationRule.getEntity(productId)).thenReturn(product);
        when(participationExistenceValidationRule.getEntity(productId, userId)).thenReturn(participation);

        // when
        participationService.leaveProduct(productId, userId);

        // then
        verify(participationRepository, times(1)).delete(participation);
        verify(productRepository, times(1)).save(product);
        assertThat(product.getParticipants()).isEqualTo(-1);
    }

    @DisplayName("상품 참여 취소 시 상품이 존재하지 않으면 예외 발생")
    @Test
    void shouldThrowException_WhenProductDoesNotExistForLeave() {
        // given
        when(productExistenceValidationRule.getEntity(productId)).thenThrow(new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> participationService.leaveProduct(productId, userId));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("상품 참여 취소 시 참여 기록이 존재하지 않으면 예외 발생")
    @Test
    void shouldThrowException_WhenParticipationDoesNotExistForLeave() {
        // given
        when(productExistenceValidationRule.getEntity(productId)).thenReturn(product);
        when(participationExistenceValidationRule.getEntity(productId, userId)).thenThrow(new ParticipationException(ParticipationErrorCode.NOT_PARTICIPATED));

        // when & then
        ParticipationException exception = assertThrows(ParticipationException.class, () -> participationService.leaveProduct(productId, userId));
        assertThat(exception.getMessage()).isEqualTo(ParticipationErrorCode.NOT_PARTICIPATED.getMessage());
    }
}
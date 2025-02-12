package com.luckymarket.application.validation.product;

import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductValidationRuleTest {
    private ProductValidationRule productValidationRule;

    @BeforeEach
    void setUp() {
        productValidationRule = new ProductValidationRule();
    }

    @DisplayName("제목이 비어있을 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenTitleIsBlank() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("")
                .description("Description")
                .price(BigDecimal.TEN)
                .categoryCode("A001")
                .maxParticipants(10)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.TITLE_BLANK.getMessage());
    }

    @DisplayName("설명이 비어있을 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenDescriptionIsBlank() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("")
                .price(BigDecimal.TEN)
                .categoryCode("A001")
                .maxParticipants(10)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.DESCRIPTION_BLANK.getMessage());
    }

    @DisplayName("가격이 0 이하일 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenPriceIsZeroOrNegative() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("Description")
                .price(BigDecimal.ZERO)
                .categoryCode("A001")
                .maxParticipants(10)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.INVALID_PRICE.getMessage());
    }

    @DisplayName("카테고리 코드가 비어있을 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenCategoryCodeIsBlank() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("Description")
                .price(BigDecimal.TEN)
                .categoryCode("")
                .maxParticipants(10)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.CATEGORY_BLANK.getMessage());
    }

    @DisplayName("최대 참여 인원이 0 이하일 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenMaxParticipantsIsZeroOrNegative() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("Description")
                .price(BigDecimal.TEN)
                .categoryCode("A001")
                .maxParticipants(0)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.MAX_PARTICIPANTS_INVALID.getMessage());
    }

    @DisplayName("종료 날짜가 현재보다 이전일 때 예외가 발생하는지 테스트")
    @Test
    void should_ThrowException_WhenEndDateIsBeforeToday() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("Description")
                .price(BigDecimal.TEN)
                .categoryCode("A001")
                .maxParticipants(10)
                .endDate(LocalDate.now().minusDays(1))
                .build();

        // when, then
        ProductException nullException = assertThrows(ProductException.class, () -> productValidationRule.validate(dto));
        assertThat(nullException.getMessage()).isEqualTo(ProductErrorCode.DATE_INVALID.getMessage());
    }

    @DisplayName("모든 값이 유효할 때 예외가 발생하지 않는지 테스트")
    @Test
    void should_NotThrowException_WhenAllFieldsAreValid() {
        // given
        ProductCreateDto dto = ProductCreateDto.builder()
                .title("Title")
                .description("Description")
                .price(BigDecimal.TEN)
                .categoryCode("A001")
                .maxParticipants(10)
                .endDate(LocalDate.now().plusDays(1))
                .build();

        // when, then
        assertDoesNotThrow(() -> productValidationRule.validate(dto));
    }
}

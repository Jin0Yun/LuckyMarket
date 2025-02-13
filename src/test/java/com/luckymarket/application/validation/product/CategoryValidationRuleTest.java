package com.luckymarket.application.validation.product;

import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.exception.product.CategoryErrorCode;
import com.luckymarket.domain.exception.product.CategoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryValidationRuleTest {
    private CategoryRepository categoryRepository;
    private CategoryValidationRule categoryValidationRule;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryValidationRule = new CategoryValidationRule(categoryRepository);
    }

    @DisplayName("카테고리 목록이 null일 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenCategoriesIsNull() {
        // given
        List<Category> nullCategories = null;

        // when & then
        CategoryException nullException = assertThrows(CategoryException.class, () -> categoryValidationRule.validate(nullCategories));
        assertThat(nullException.getMessage()).isEqualTo(CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("카테고리 목록이 비어있을 경우 예외를 던진다.")
    @Test
    void validate_ShouldThrowException_WhenCategoriesIsEmpty() {
        // given
        List<Category> emptyCategories = Collections.emptyList();

        // when & then
        CategoryException emptyException = assertThrows(CategoryException.class, () -> categoryValidationRule.validate(emptyCategories));
        assertThat(emptyException.getMessage()).isEqualTo(CategoryErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("카테고리 목록이 유효할 경우 예외를 던지지 않는다.")
    @Test
    void validate_ShouldNotThrowException_WhenCategoriesAreValid() {
        // given
        List<Category> validCategories = List.of(new Category(), new Category());

        // when & then
        assertDoesNotThrow(() -> categoryValidationRule.validate(validCategories));
    }

    @DisplayName("상위 카테고리가 존재하지 않을 경우 예외를 던진다.")
    @Test
    void validateParentCategoryExists_ShouldThrowException_WhenParentCategoryDoesNotExist() {
        // given
        Long parentCategoryId = 1L;
        boolean parentExists = false;

        // when & then
        CategoryException exception = assertThrows(CategoryException.class, () -> categoryValidationRule.validateParentCategoryExists(parentCategoryId));
        assertThat(exception.getMessage()).isEqualTo(CategoryErrorCode.PARENT_CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("상위 카테고리가 존재할 경우 예외를 던지지 않는다.")
    @Test
    void validateParentCategoryExists_ShouldNotThrowException_WhenParentCategoryExists() {
        // given
        Long parentCategoryId = 1L;
        when(categoryRepository.existsById(parentCategoryId)).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> categoryValidationRule.validateParentCategoryExists(parentCategoryId));
    }

    @DisplayName("하위 카테고리 목록이 null일 경우 예외를 던진다.")
    @Test
    void validateSubCategoriesExist_ShouldThrowException_WhenSubCategoriesAreNull() {
        // given
        List<Category> nullSubCategories = null;

        // when & then
        CategoryException nullException = assertThrows(CategoryException.class, () -> categoryValidationRule.validateSubCategoriesExist(nullSubCategories));
        assertThat(nullException.getMessage()).isEqualTo(CategoryErrorCode.NO_SUBCATEGORY_FOUND.getMessage());
    }

    @DisplayName("하위 카테고리 목록이 비어있을 경우 예외를 던진다.")
    @Test
    void validateSubCategoriesExist_ShouldThrowException_WhenSubCategoriesAreEmpty() {
        // given
        List<Category> emptySubCategories = Collections.emptyList();

        // when & then
        CategoryException emptyException = assertThrows(CategoryException.class, () -> categoryValidationRule.validateSubCategoriesExist(emptySubCategories));
        assertThat(emptyException.getMessage()).isEqualTo(CategoryErrorCode.NO_SUBCATEGORY_FOUND.getMessage());
    }

    @DisplayName("하위 카테고리 목록이 유효할 경우 예외를 던지지 않는다.")
    @Test
    void validateSubCategoriesExist_ShouldNotThrowException_WhenSubCategoriesAreValid() {
        // given
        List<Category> validSubCategories = List.of(new Category(), new Category());

        // when & then
        assertDoesNotThrow(() -> categoryValidationRule.validateSubCategoriesExist(validSubCategories));
    }

    @DisplayName("카테고리 코드가 null일 경우 예외를 던진다.")
    @Test
    void validateCategoryCodeExists_ShouldThrowException_WhenCategoryCodeDoesNotExist() {
        // given
        Category nullCategory = null;

        // when & then
        CategoryException exception = assertThrows(CategoryException.class, () -> categoryValidationRule.validateCategoryCodeExists(nullCategory));
        assertThat(exception.getMessage()).isEqualTo(CategoryErrorCode.CATEGORY_CODE_NOT_FOUND.getMessage());
    }

    @DisplayName("카테고리 코드가 존재할 경우 예외를 던지지 않는다.")
    @Test
    void validateCategoryCodeExists_ShouldNotThrowException_WhenCategoryCodeExists() {
        // given
        Category validCategory = new Category();

        // when & then
        assertDoesNotThrow(() -> categoryValidationRule.validateCategoryCodeExists(validCategory));
    }
}
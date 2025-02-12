package com.luckymarket.application.service.product.impl;

import com.luckymarket.application.validation.product.CategoryValidationRule;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidationRule categoryValidationRule;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category parentCategory;
    private Category childCategory1;
    private Category childCategory2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 부모 카테고리
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setCode("A000");
        parentCategory.setName("과일");
        parentCategory.setParent(null);

        // 자식 카테고리들
        childCategory1 = new Category();
        childCategory1.setId(9L);
        childCategory1.setCode("A001");
        childCategory1.setName("사과");
        childCategory1.setParent(1L);

        childCategory2 = new Category();
        childCategory2.setId(10L);
        childCategory2.setCode("A002");
        childCategory2.setName("배");
        childCategory2.setParent(1L);
    }

    @DisplayName("전체 카테고리 조회 테스트")
    @Test
    void should_RetrieveAllCategoriesSuccessfully() {
        // given
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(parentCategory, childCategory1, childCategory2));

        // when
        List<Category> result = categoryService.getCategories();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(parentCategory, childCategory1, childCategory2);
        verify(categoryValidationRule).validate(result);
    }

    @DisplayName("부모 카테고리를 조회하는지 테스트")
    @Test
    void should_RetrieveParentCategoriesSuccessfully() {
        // given
        when(categoryRepository.findByParentIsNull()).thenReturn(Arrays.asList(parentCategory));

        // when
        List<Category> result = categoryService.getParentCategories();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("과일");
        verify(categoryValidationRule).validate(result);
    }

    @DisplayName("하위 카테고리를 조회하는지 테스트")
    @Test
    void should_RetrieveSubCategoriesSuccessfully() {
        // given
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findByParent(1L)).thenReturn(Arrays.asList(childCategory1, childCategory2));

        // when
        List<Category> result = categoryService.getSubCategories(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(childCategory1, childCategory2);
        verify(categoryValidationRule).validateParentCategoryExists(true);
        verify(categoryValidationRule).validateSubCategoriesExist(result);
    }

    @DisplayName("특정 카테고리 코드로 조회되는지 테스트")
    @Test
    void should_RetrieveCategoryByCodeSuccessfully() {
        // given
        String code = "A001";
        when(categoryRepository.findByCode(code)).thenReturn(Optional.of(childCategory1));

        // when
        Category result = categoryService.getCategoryByCode(code);

        // then
        assertThat(result).isEqualTo(childCategory1);
        verify(categoryValidationRule).validateCategoryCodeExists(childCategory1);
    }
}

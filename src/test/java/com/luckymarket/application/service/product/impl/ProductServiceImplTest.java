package com.luckymarket.application.service.product.impl;

import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.application.service.product.ProductSearchService;
import com.luckymarket.application.validation.participation.ProductExistenceValidationRule;
import com.luckymarket.application.validation.participation.UserExistenceValidationRule;
import com.luckymarket.application.validation.product.CategoryValidationRule;
import com.luckymarket.application.validation.product.ProductValidationRule;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.application.dto.product.ProductCreateRequest;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductValidationRule productValidationRule;

    @Mock
    private CategoryValidationRule categoryValidationRule;

    @Mock
    private ProductExistenceValidationRule productExistenceValidationRule;

    @Mock
    private UserExistenceValidationRule userExistenceValidationRule;

    @Mock
    private ProductSearchService productSearchService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Member member;
    private ProductCreateRequest validProductCreateRequest;
    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId(1L);
        member.setEmail("test@example.com");
        member.setPassword("encodedPassword");

        category = new Category();
        category.setName("과일");
        category.setCode("A000");

        validProductCreateRequest = ProductCreateRequest.builder()
                .title("신선한 사과")
                .description("100% 유기농 사과, 맛있고 건강한 사과입니다.")
                .price(BigDecimal.valueOf(5000))
                .categoryCode("A000")
                .status(ProductStatus.ONGOING)
                .endDate(LocalDate.of(2025, 3, 10))
                .build();

        product = ProductMapper.toEntity(validProductCreateRequest, member, category);
    }

    @DisplayName("존재하지 않는 카테고리 코드로 상품을 생성하려고 하면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenCategoryNotFoundForCreate() {
        // given
        doNothing().when(userExistenceValidationRule).validate(1L);
        when(categoryValidationRule.getCategory("A000")).thenThrow(new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.createProduct(validProductCreateRequest, 1L));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하지 않는 사용자가 상품을 생성하려고 하면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenUserNotFoundForCreate() {
        // given
        doThrow(new AuthException(AuthErrorCode.USER_NOT_FOUND)).when(userExistenceValidationRule).validate(1L);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> productService.createProduct(validProductCreateRequest, 1L));
        assertThat(exception.getMessage()).isEqualTo(AuthErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("상품을 정상적으로 생성한다.")
    @Test
    void should_CreateProductSuccessfully() {
        // given
        doNothing().when(userExistenceValidationRule).validate(1L);
        when(userExistenceValidationRule.getEntity(1L)).thenReturn(member);
        when(categoryValidationRule.getCategory("A000")).thenReturn(category);

        // when
        productService.createProduct(validProductCreateRequest, 1L);

        // then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("상품을 수정할 때 상품이 존재하지 않으면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenProductNotFoundForUpdate() {
        // given
        when(productExistenceValidationRule.getEntity(1L)).thenThrow(new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.updateProduct(1L, validProductCreateRequest, 1L));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("상품을 수정할 때 수정 권한이 없으면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenUnauthorizedUserForUpdate() {
        // given
        Product existingProduct = Product.builder()
                .id(1L)
                .member(member)
                .build();
        when(productExistenceValidationRule.getEntity(1L)).thenReturn(existingProduct);
        Long unauthorizedUserId = 2L;

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.updateProduct(1L, validProductCreateRequest, unauthorizedUserId));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.UNAUTHORIZED_PRODUCT_MODIFY.getMessage());
    }

    @DisplayName("존재하지 않는 카테고리로 상품을 수정하려고 하면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenCategoryNotFoundForUpdate() {
        // given
        Product existingProduct = Product.builder().id(1L).member(member).build();
        when(productExistenceValidationRule.getEntity(1L)).thenReturn(existingProduct);
        when(categoryValidationRule.getCategory("A000")).thenThrow(new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.updateProduct(1L, validProductCreateRequest, 1L));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("상품을 정상적으로 수정한다.")
    @Test
    void should_UpdateProductSuccessfully() {
        // given
        Product existingProduct = Product.builder().id(1L).member(member).build();
        when(productExistenceValidationRule.getEntity(1L)).thenReturn(existingProduct);
        when(categoryValidationRule.getCategory("A000")).thenReturn(category);

        // when
        productService.updateProduct(1L, validProductCreateRequest, 1L);

        // then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("존재하지 않는 상품을 삭제하려고 하면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenProductNotFoundForDelete() {
        // given
        when(productExistenceValidationRule.getEntity(1L)).thenThrow(new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.deleteProduct(1L, 1L));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("상품을 삭제할 때 삭제 권한이 없으면 예외를 던진다.")
    @Test
    void should_ThrowException_WhenUnauthorizedUserForDelete() {
        // given
        Product existingProduct = Product.builder()
                .id(1L)
                .member(member)
                .build();
        when(productExistenceValidationRule.getEntity(1L)).thenReturn(existingProduct);
        Long unauthorizedUserId = 2L;

        // when & then
        ProductException exception = assertThrows(ProductException.class, () -> productService.deleteProduct(1L, unauthorizedUserId));
        assertThat(exception.getMessage()).isEqualTo(ProductErrorCode.UNAUTHORIZED_PRODUCT_DELETE.getMessage());
    }

    @DisplayName("상품을 정상적으로 삭제한다.")
    @Test
    void should_DeleteProductSuccessfully() {
        // given
        Product existingProduct = Product.builder().id(1L).member(member).build();
        when(productExistenceValidationRule.getEntity(1L)).thenReturn(existingProduct);

        // when
        productService.deleteProduct(1L, 1L);

        // then
        verify(productRepository, times(1)).delete(existingProduct);
    }

    @DisplayName("상품 검색을 호출하면 검색 서비스의 search 메서드가 호출되고 결과를 반환한다.")
    @Test
    void should_CallSearchMethodAndReturnResults_WhenSearchProductsIsCalled() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                "상품명", "A001", BigDecimal.valueOf(1000), BigDecimal.valueOf(5000), null
        );
        List<Product> expectedProducts = Arrays.asList(product);
        when(productSearchService.search(searchRequest)).thenReturn(expectedProducts);

        // when
        List<Product> result = productService.searchProducts(searchRequest);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("신선한 사과");
        verify(productSearchService, times(1)).search(searchRequest);
    }
}
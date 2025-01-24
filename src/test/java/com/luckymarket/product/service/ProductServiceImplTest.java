package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;
import com.luckymarket.product.domain.Product;
import com.luckymarket.product.domain.ProductStatus;
import com.luckymarket.product.dto.ProductCreateDto;
import com.luckymarket.product.exception.ProductErrorCode;
import com.luckymarket.product.exception.ProductException;
import com.luckymarket.product.repository.CategoryRepository;
import com.luckymarket.product.repository.ProductRepository;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductCreateDto validProductCreateDto;
    private Member member;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validProductCreateDto = ProductCreateDto.builder()
                .title("신선한 사과")
                .description("100% 유기농 사과, 맛있고 건강한 사과입니다.")
                .price(BigDecimal.valueOf(5000))
                .categoryCode("A000")
                .status(ProductStatus.ONGOING)
                .maxParticipants(100)
                .endDate(LocalDate.of(2025, 3, 10))
                .build();

        member = new Member();
        member.setId(1L);

        category = new Category();
        category.setCode("A000");
        category.setName("과일");

        product = Product.builder()
                .id(1L)
                .title("신선한 사과")
                .description("100% 유기농 사과")
                .price(BigDecimal.valueOf(5000))
                .category(category)
                .status(ProductStatus.ONGOING)
                .maxParticipants(100)
                .endDate(LocalDate.of(2025, 3, 10))
                .imageUrl("image-url")
                .member(member)
                .build();
    }

    @DisplayName("상품 등록이 성공하는지 확인하는 테스트")
    @Test
    void shouldCreateProductSuccessfully() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(categoryRepository.findByCode("A000")).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        // when
        Product product = productService.createProduct(validProductCreateDto, 1L);

        // then
        assertThat(product).isNotNull();
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @DisplayName("존재하지 않는 사용자로 상품 등록 시 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.createProduct(validProductCreateDto, 1L))
                .isInstanceOf(UserException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하지 않는 카테고리로 상품 등록 시 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(categoryRepository.findByCode("A000")).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> productService.createProduct(validProductCreateDto, 1L))
                .isInstanceOf(ProductException.class)
                .hasMessage(ProductErrorCode.CATEGORY_NOT_FOUND.getMessage());
    }

    @DisplayName("상품 제목이 비어있을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(categoryRepository.findByCode("A000")).thenReturn(category);
        validProductCreateDto.setTitle("");

        // when & then
        assertThatThrownBy(() -> productService.createProduct(validProductCreateDto, 1L))
                .isInstanceOf(ProductException.class)
                .hasMessage(ProductErrorCode.TITLE_BLANK.getMessage());
    }

    @DisplayName("상품 가격이 0보다 작을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(categoryRepository.findByCode("A000")).thenReturn(category);
        validProductCreateDto.setPrice(BigDecimal.valueOf(0));

        // when & then
        assertThatThrownBy(() -> productService.createProduct(validProductCreateDto, 1L))
                .isInstanceOf(ProductException.class)
                .hasMessage(ProductErrorCode.INVALID_PRICE.getMessage());
    }

    @DisplayName("상품 종료일이 유효하지 않을 경우 예외가 발생하는지 확인하는 테스트")
    @Test
    void shouldThrowExceptionWhenEndDateIsInvalid() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(categoryRepository.findByCode("A000")).thenReturn(category);
        validProductCreateDto.setEndDate(LocalDate.of(2023, 3, 10));

        // when & then
        assertThatThrownBy(() -> productService.createProduct(validProductCreateDto, 1L))
                .isInstanceOf(ProductException.class)
                .hasMessage(ProductErrorCode.DATE_INVALID.getMessage());
    }

    @DisplayName("상품이 조회되는지 확인하는 테스트")
    @Test
    void shouldGetProductByIdSuccessfully() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        Product foundProduct = productService.getProductById(1L);

        // then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getId()).isEqualTo(1L);
    }

    @DisplayName("전체 상품 목록이 조회되는지 확인하는 테스트")
    @Test
    void shouldGetAllProductsSuccessfully() {
        // given
        when(productRepository.findAll()).thenReturn(List.of(product));

        // when
        List<Product> products = productService.getAllProducts();

        // then
        assertThat(products).isNotEmpty();
        assertThat(products).contains(product);
    }
}

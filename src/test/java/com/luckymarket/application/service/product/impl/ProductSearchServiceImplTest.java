package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.adapter.out.persistence.product.search.strategy.CategoryCodeSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.PriceSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.ProductStatusSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.TitleSearchStrategy;
import com.luckymarket.application.dto.product.ProductCreateRequest;
import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.PriceRange;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.domain.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductSearchServiceImplTest {
    @Mock
    private TitleSearchStrategy titleSearchStrategy;

    @Mock
    private CategoryCodeSearchStrategy categoryCodeSearchStrategy;

    @Mock
    private PriceSearchStrategy priceSearchStrategy;

    @Mock
    private ProductStatusSearchStrategy productStatusSearchStrategy;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchServiceImpl productSearchService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ProductCreateRequest validProductCreateRequest = ProductCreateRequest.builder()
                .title("신선한 사과")
                .description("100% 유기농 사과, 맛있고 건강한 사과입니다.")
                .price(BigDecimal.valueOf(5000))
                .categoryCode("A000")
                .status(ProductStatus.ONGOING)
                .endDate(LocalDate.of(2025, 3, 10))
                .build();

        product = ProductMapper.toEntity(validProductCreateRequest, new Member(), new Category());
    }


    @DisplayName("상품을 제목으로 검색한다.")
    @Test
    void should_SearchProductByTitle() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                "신선한 사과", null, null, null, null
        );
        when(titleSearchStrategy.apply("신선한 사과")).thenReturn(Specification.where(null));
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        // when
        List<Product> products = productSearchService.search(searchRequest);

        // then
        assertThat(products).isNotEmpty();
    }

    @DisplayName("상품을 카테고리 코드로 검색한다.")
    @Test
    void should_SearchProductByCategoryCode() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                null, "A000", null, null, null
        );
        when(categoryCodeSearchStrategy.apply("A000")).thenReturn(Specification.where(null));
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        // when
        List<Product> products = productSearchService.search(searchRequest);

        // then
        assertThat(products).isNotEmpty();
    }

    @DisplayName("상품을 가격으로 검색한다.")
    @Test
    void should_SearchProductByPriceRange() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                null, null, BigDecimal.valueOf(1000), BigDecimal.valueOf(5000), null
        );
        when(priceSearchStrategy.apply(new PriceRange(BigDecimal.valueOf(1000), BigDecimal.valueOf(5000)))).thenReturn(Specification.where(null));
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        // when
        List<Product> products = productSearchService.search(searchRequest);

        // then
        assertThat(products).isNotEmpty();
    }

    @DisplayName("상태로만 상품을 검색한다.")
    @Test
    void should_ReturnProducts_WhenSearchByStatus() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                null, null, null, null, ProductStatus.ONGOING
        );
        when(productStatusSearchStrategy.apply(ProductStatus.ONGOING)).thenReturn(Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ProductStatus.ONGOING)));
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        // when
        List<Product> products = productSearchService.search(searchRequest);

        // then
        assertThat(products).isNotEmpty();
    }

    @DisplayName("여러 조건으로 상품을 검색한다.")
    @Test
    void should_ReturnProducts_WhenSearchWithMultipleCriteria() {
        // given
        ProductSearchRequest searchRequest = new ProductSearchRequest(
                "상품명", "A001", BigDecimal.valueOf(1000), BigDecimal.valueOf(5000), null
        );
        when(titleSearchStrategy.apply("상품명")).thenReturn(Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("title"), "상품명")));
        when(categoryCodeSearchStrategy.apply("A001")).thenReturn(Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryCode"), "A001")));
        when(priceSearchStrategy.apply(new PriceRange(BigDecimal.valueOf(1000), BigDecimal.valueOf(5000)))).thenReturn(Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), BigDecimal.valueOf(1000), BigDecimal.valueOf(5000))));
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));

        // when
        List<Product> products = productSearchService.search(searchRequest);

        // then
        assertThat(products).isNotEmpty();
    }
}
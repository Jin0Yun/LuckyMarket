package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.adapter.out.persistence.product.search.strategy.CategoryCodeSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.PriceSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.ProductStatusSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.TitleSearchStrategy;
import com.luckymarket.application.service.product.ProductService;
import com.luckymarket.application.validation.product.ProductValidationRule;
import com.luckymarket.domain.entity.product.PriceRange;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import com.luckymarket.domain.mapper.ProductMapper;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.adapter.out.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductValidationRule productValidationRule;
    private final TitleSearchStrategy titleSearchStrategy;
    private final CategoryCodeSearchStrategy categoryCodeSearchStrategy;
    private final PriceSearchStrategy priceSearchStrategy;
    private final ProductStatusSearchStrategy productStatusSearchStrategy;

    @Override
    public Product createProduct(ProductCreateDto productCreateDto, Long userId) {
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));
        Category category = categoryRepository.findByCode(productCreateDto.getCategoryCode())
                .orElseThrow(() -> new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND));

        productValidationRule.validate(productCreateDto);
        Product product = ProductMapper.toEntity(productCreateDto, member, category);

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Long productId, ProductCreateDto productCreateDto, Long userId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (!existingProduct.getMember().getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_MODIFY);
        }

        Category category = categoryRepository.findByCode(productCreateDto.getCategoryCode())
                .orElseThrow(() -> new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND));

        productValidationRule.validate(productCreateDto);
        ProductMapper.updateEntity(existingProduct, productCreateDto, category);
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getMember().getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_DELETE);
        }

        productRepository.delete(product);
    }

    @Override
    public List<Product> searchProducts(String title, String categoryCode, BigDecimal priceMin, BigDecimal priceMax, ProductStatus status) {
        Specification<Product> spec = Specification
                .where(titleSearchStrategy.apply(title))
                .and(categoryCodeSearchStrategy.apply(categoryCode))
                .and(priceSearchStrategy.apply(new PriceRange(priceMin, priceMax)))
                .and(productStatusSearchStrategy.apply(status));

        return productRepository.findAll(spec);
    }
}

package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.CategoryRepository;
import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.adapter.out.persistence.product.ProductSpecification;
import com.luckymarket.application.service.product.ProductService;
import com.luckymarket.domain.exception.auth.AuthErrorCode;
import com.luckymarket.domain.exception.auth.AuthException;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import com.luckymarket.domain.mapper.ProductMapper;
import com.luckymarket.domain.entity.user.Member;
import com.luckymarket.adapter.out.persistence.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(ProductCreateDto productCreateDto, Long userId) {
        Member member = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findByCode(productCreateDto.getCategoryCode());

        if (category == null) {
            throw new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND);
        }

        validateProductData(productCreateDto);
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

        Category category = categoryRepository.findByCode(productCreateDto.getCategoryCode());
        if (category == null) {
            throw new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND);
        }

        existingProduct.setTitle(productCreateDto.getTitle());
        existingProduct.setDescription(productCreateDto.getDescription());
        existingProduct.setPrice(productCreateDto.getPrice());
        existingProduct.setCategory(category);
        existingProduct.setStatus(productCreateDto.getStatus());
        existingProduct.setMaxParticipants(productCreateDto.getMaxParticipants());
        existingProduct.setEndDate(productCreateDto.getEndDate());
        existingProduct.setImageUrl(productCreateDto.getImageUrl());

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
    public List<Product> searchProducts(String title, String categoryCode, BigDecimal priceMin, BigDecimal priceMax, String status) {
        Specification<Product> spec = Specification.where(ProductSpecification.hasTitle(title))
                .and(ProductSpecification.hasCategoryCode(categoryCode))
                .and(ProductSpecification.hasPriceBetween(priceMin, priceMax))
                .and(ProductSpecification.hasStatus(status != null ? ProductStatus.valueOf(status) : null));

        List<Product> products = productRepository.findAll(spec);

        if (products.isEmpty()) {
            throw new ProductException(ProductErrorCode.NO_SEARCH_RESULTS);
        }

        return products;
    }

    private void validateProductData(ProductCreateDto productCreateDto) {
        if (productCreateDto.getTitle() == null || productCreateDto.getTitle().isEmpty()) {
            throw new ProductException(ProductErrorCode.TITLE_BLANK);
        }
        if (productCreateDto.getDescription() == null || productCreateDto.getDescription().isEmpty()) {
            throw new ProductException(ProductErrorCode.DESCRIPTION_BLANK);
        }
        if (productCreateDto.getPrice() == null || productCreateDto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRICE);
        }
        if (productCreateDto.getCategoryCode() == null || productCreateDto.getCategoryCode().isEmpty()) {
            throw new ProductException(ProductErrorCode.CATEGORY_BLANK);
        }
        if (productCreateDto.getMaxParticipants() <= 0) {
            throw new ProductException(ProductErrorCode.MAX_PARTICIPANTS_INVALID);
        }
        if (productCreateDto.getEndDate() == null || productCreateDto.getEndDate().isBefore(LocalDate.now())) {
            throw new ProductException(ProductErrorCode.DATE_INVALID);
        }
    }
}

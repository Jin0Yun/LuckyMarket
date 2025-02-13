package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.application.service.product.ProductSearchService;
import com.luckymarket.application.service.product.ProductService;
import com.luckymarket.application.validation.participation.UserExistenceValidationRule;
import com.luckymarket.application.validation.participation.ProductExistenceValidationRule;
import com.luckymarket.application.validation.product.CategoryValidationRule;
import com.luckymarket.application.validation.product.ProductValidationRule;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateRequest;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import com.luckymarket.domain.mapper.ProductMapper;
import com.luckymarket.domain.entity.user.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductValidationRule productValidationRule;
    private final CategoryValidationRule categoryValidationRule;
    private final ProductExistenceValidationRule productExistenceValidationRule;
    private final UserExistenceValidationRule userExistenceValidationRule;
    private final ProductSearchService productSearchService;

    @Override
    public Product createProduct(ProductCreateRequest productCreateRequest, Long userId) {
        userExistenceValidationRule.validate(userId);
        Category category = categoryValidationRule.getCategory(productCreateRequest.getCategoryCode());
        productValidationRule.validate(productCreateRequest);

        Member member = userExistenceValidationRule.getEntity(userId);
        Product product = ProductMapper.toEntity(productCreateRequest, member, category);

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) {
        return productExistenceValidationRule.getEntity(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Long productId, ProductCreateRequest productCreateRequest, Long userId) {
        Product existingProduct = getProductById(productId);

        if (!existingProduct.getMember().getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_MODIFY);
        }

        Category category = categoryValidationRule.getCategory(productCreateRequest.getCategoryCode());
        productValidationRule.validate(productCreateRequest);
        ProductMapper.updateEntity(existingProduct, productCreateRequest, category);
        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId, Long userId) {
        Product product = getProductById(productId);
        if (!product.getMember().getId().equals(userId)) {
            throw new ProductException(ProductErrorCode.UNAUTHORIZED_PRODUCT_DELETE);
        }
        productRepository.delete(product);
    }

    @Override
    public List<Product> searchProducts(ProductSearchRequest searchRequest) {
        return productSearchService.search(searchRequest);
    }
}

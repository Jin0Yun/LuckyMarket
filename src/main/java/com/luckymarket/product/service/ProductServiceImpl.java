package com.luckymarket.product.service;

import com.luckymarket.product.domain.Category;
import com.luckymarket.product.domain.Product;
import com.luckymarket.product.dto.ProductCreateDto;
import com.luckymarket.product.exception.ProductErrorCode;
import com.luckymarket.product.exception.ProductException;
import com.luckymarket.product.mapper.ProductMapper;
import com.luckymarket.product.repository.CategoryRepository;
import com.luckymarket.product.repository.ProductRepository;
import com.luckymarket.user.domain.Member;
import com.luckymarket.user.exception.UserErrorCode;
import com.luckymarket.user.exception.UserException;
import com.luckymarket.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

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

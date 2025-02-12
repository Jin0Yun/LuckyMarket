package com.luckymarket.application.service.product;

import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.entity.product.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product createProduct(ProductCreateDto productCreateDto, Long userId);
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    Product updateProduct(Long productId, ProductCreateDto productCreateDto, Long userId);
    void deleteProduct(Long productId, Long userId);
    List<Product> searchProducts(String title, String categoryCode, BigDecimal priceMin, BigDecimal priceMax, ProductStatus status);
}

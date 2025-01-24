package com.luckymarket.product.service;

import com.luckymarket.product.domain.Product;
import com.luckymarket.product.dto.ProductCreateDto;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product createProduct(ProductCreateDto productCreateDto, Long userId);
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    Product updateProduct(Long productId, ProductCreateDto productCreateDto, Long userId);
    void deleteProduct(Long productId, Long userId);
    List<Product> searchProducts(String title, String categoryCode, BigDecimal priceMin, BigDecimal priceMax, String status);
}

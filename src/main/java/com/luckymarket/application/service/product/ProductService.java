package com.luckymarket.application.service.product;

import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateRequest;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductCreateRequest productCreateRequest, Long userId);
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    Product updateProduct(Long productId, ProductCreateRequest productCreateRequest, Long userId);
    void deleteProduct(Long productId, Long userId);
    List<Product> searchProducts(ProductSearchRequest criteria);
}

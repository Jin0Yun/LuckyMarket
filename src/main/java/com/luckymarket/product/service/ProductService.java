package com.luckymarket.product.service;

import com.luckymarket.product.domain.Product;
import com.luckymarket.product.dto.ProductCreateDto;

public interface ProductService {
    Product createProduct(ProductCreateDto productCreateDto, Long userId);
}

package com.luckymarket.application.service.product;

import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.domain.entity.product.Product;

import java.util.List;

public interface ProductSearchService {
    List<Product> search(ProductSearchRequest searchRequest);
}
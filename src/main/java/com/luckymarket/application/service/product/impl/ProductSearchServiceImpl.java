package com.luckymarket.application.service.product.impl;

import com.luckymarket.adapter.out.persistence.product.ProductRepository;
import com.luckymarket.adapter.out.persistence.product.search.strategy.CategoryCodeSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.PriceSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.ProductStatusSearchStrategy;
import com.luckymarket.adapter.out.persistence.product.search.strategy.TitleSearchStrategy;
import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.application.service.product.ProductSearchService;
import com.luckymarket.domain.entity.product.PriceRange;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.exception.product.ProductErrorCode;
import com.luckymarket.domain.exception.product.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {
    private final TitleSearchStrategy titleSearchStrategy;
    private final CategoryCodeSearchStrategy categoryCodeSearchStrategy;
    private final PriceSearchStrategy priceSearchStrategy;
    private final ProductStatusSearchStrategy productStatusSearchStrategy;
    private final ProductRepository productRepository;

    public List<Product> search(ProductSearchRequest searchRequest) {
        Specification<Product> spec = Specification
                .where(titleSearchStrategy.apply(searchRequest.getTitle()))
                .and(categoryCodeSearchStrategy.apply(searchRequest.getCategoryCode()))
                .and(priceSearchStrategy.apply(new PriceRange(searchRequest.getPriceMin(), searchRequest.getPriceMax())))
                .and(productStatusSearchStrategy.apply(searchRequest.getStatus()));

        List<Product> products = productRepository.findAll(spec);

        if (products.isEmpty()) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        return products;
    }
}
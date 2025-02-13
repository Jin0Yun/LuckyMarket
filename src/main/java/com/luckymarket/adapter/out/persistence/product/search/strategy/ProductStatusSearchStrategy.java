package com.luckymarket.adapter.out.persistence.product.search.strategy;

import com.luckymarket.adapter.out.persistence.product.search.ProductSearchStrategy;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.domain.entity.product.ProductStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProductStatusSearchStrategy implements ProductSearchStrategy<ProductStatus> {
    @Override
    public Specification<Product> apply(ProductStatus status) {
        return (root, query, cb) ->
                (status == null)
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }
}

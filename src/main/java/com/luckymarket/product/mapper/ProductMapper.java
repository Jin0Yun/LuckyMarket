package com.luckymarket.product.mapper;

import com.luckymarket.product.domain.Category;
import com.luckymarket.product.domain.Product;
import com.luckymarket.product.dto.ProductCreateDto;
import com.luckymarket.user.domain.Member;

public class ProductMapper {
    public static Product toEntity(ProductCreateDto dto, Member member, Category category) {
        return Product.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(category)
                .status(dto.getStatus())
                .maxParticipants(dto.getMaxParticipants())
                .endDate(dto.getEndDate())
                .imageUrl(dto.getImageUrl())
                .member(member)
                .build();
    }
}
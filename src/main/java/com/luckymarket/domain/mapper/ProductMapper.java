package com.luckymarket.domain.mapper;

import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.domain.entity.user.Member;
import org.springframework.stereotype.Component;

@Component
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

    public static void updateEntity(Product product, ProductCreateDto dto, Category category) {
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);
        product.setStatus(dto.getStatus());
        product.setMaxParticipants(dto.getMaxParticipants());
        product.setEndDate(dto.getEndDate());
        product.setImageUrl(dto.getImageUrl());
    }
}
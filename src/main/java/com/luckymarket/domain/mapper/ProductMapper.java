package com.luckymarket.domain.mapper;

import com.luckymarket.application.dto.user.ProductParticipantInfo;
import com.luckymarket.application.dto.user.UserParticipatedProductResponse;
import com.luckymarket.application.dto.user.UserProductSummaryResponse;
import com.luckymarket.domain.entity.participation.Participation;
import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateRequest;
import com.luckymarket.domain.entity.user.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    public static Product toEntity(ProductCreateRequest dto, Member member, Category category) {
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

    public static void updateEntity(Product product, ProductCreateRequest dto, Category category) {
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(category);
        product.setStatus(dto.getStatus());
        product.setMaxParticipants(dto.getMaxParticipants());
        product.setEndDate(dto.getEndDate());
        product.setImageUrl(dto.getImageUrl());
    }

    public UserProductSummaryResponse toProductSummaryResponse(Product product) {
        return UserProductSummaryResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productPrice(product.getPrice())
                .productCategory(product.getCategory())
                .participants(product.getParticipants())
                .maxParticipants(product.getMaxParticipants())
                .productStatus(product.getStatus())
                .productImageUrl(product.getImageUrl())
                .productEndDate(product.getEndDate())
                .participantsList(getParticipantInfoList(product.getParticipations()))
                .build();
    }

    public UserParticipatedProductResponse toParticipatedProductResponse(Product product) {
        return UserParticipatedProductResponse.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .productPrice(product.getPrice())
                .productCategory(product.getCategory())
                .participants(product.getParticipants())
                .maxParticipants(product.getMaxParticipants())
                .productStatus(product.getStatus())
                .productImageUrl(product.getImageUrl())
                .productEndDate(product.getEndDate())
                .build();
    }

    private static List<ProductParticipantInfo> getParticipantInfoList(List<Participation> participations) {
        return participations.stream()
                .map(participation -> ProductParticipantInfo.builder()
                        .participantId(participation.getMember().getId())
                        .username(participation.getMember().getUsername())
                        .build())
                .collect(Collectors.toList());
    }
}
package com.luckymarket.product.controller;

import com.luckymarket.common.ApiResponseWrapper;
import com.luckymarket.product.domain.Product;
import com.luckymarket.product.dto.ProductCreateDto;
import com.luckymarket.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
@Tag(name = "상품 API", description = "상품 등록, 수정, 삭제, 조회 관련 API")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/{userId}")
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    public ApiResponseWrapper<Product> createProduct(@PathVariable Long userId, @RequestBody ProductCreateDto productCreateDto) {
        try {
            Product product = productService.createProduct(productCreateDto, userId);
            return ApiResponseWrapper.success("상품이 성공적으로 등록되었습니다", product);
        } catch (Exception e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}

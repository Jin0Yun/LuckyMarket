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

import java.util.List;

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

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다.")
    public ApiResponseWrapper<Product> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ApiResponseWrapper.success("상품을 성공적으로 조회했습니다", product);
        } catch (Exception e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public ApiResponseWrapper<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ApiResponseWrapper.success("상품 목록을 성공적으로 조회했습니다", products);
        } catch (Exception e) {
            return ApiResponseWrapper.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}

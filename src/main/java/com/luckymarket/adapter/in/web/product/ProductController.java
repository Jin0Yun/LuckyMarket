package com.luckymarket.adapter.in.web.product;

import com.luckymarket.adapter.in.web.ApiResponse;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateDto;
import com.luckymarket.application.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
@Tag(name = "상품 API", description = "상품 등록, 수정, 삭제, 조회 관련 API")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/{userId}")
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    public ApiResponse<Product> createProduct(@PathVariable Long userId, @RequestBody ProductCreateDto productCreateDto) {
        try {
            Product product = productService.createProduct(productCreateDto, userId);
            return ApiResponse.success("상품이 성공적으로 등록되었습니다", product);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다.")
    public ApiResponse<Product> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return ApiResponse.success("상품을 성공적으로 조회했습니다", product);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.NOT_FOUND.value());
        }
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public ApiResponse<List<Product>> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return ApiResponse.success("상품 목록을 성공적으로 조회했습니다", products);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품의 정보를 수정합니다.")
    public ApiResponse<Product> updateProduct(@PathVariable Long productId,
                                              @RequestBody ProductCreateDto productCreateDto,
                                              @RequestHeader("userId") Long userId) {
        try {
            Product updatedProduct = productService.updateProduct(productId, productCreateDto, userId);
            return ApiResponse.success("상품이 성공적으로 수정되었습니다", updatedProduct);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId, @RequestParam Long userId) {
        try {
            productService.deleteProduct(productId, userId);
            return ApiResponse.success("상품이 성공적으로 삭제되었습니다", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "상품 검색", description = "타이틀, 카테고리, 가격, 상태로 검색합니다.")
    public ApiResponse<List<Product>> searchProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String status) {
        try {
            List<Product> products = productService.searchProducts(title, categoryCode, priceMin, priceMax, status);
            return ApiResponse.success("상품 검색 결과를 성공적으로 조회했습니다", products);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}

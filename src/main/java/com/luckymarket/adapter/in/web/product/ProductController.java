package com.luckymarket.adapter.in.web.product;

import com.luckymarket.application.dto.ApiResponse;
import com.luckymarket.application.dto.product.ProductSearchRequest;
import com.luckymarket.domain.entity.product.Product;
import com.luckymarket.application.dto.product.ProductCreateRequest;
import com.luckymarket.application.service.product.ProductService;
import com.luckymarket.domain.entity.product.ProductStatus;
import com.luckymarket.infrastructure.security.SecurityContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "공동구매 상품 API", description = "공동구매 상품 등록, 수정, 삭제, 조회 관련 API")
public class ProductController {
    private final ProductService productService;
    private final SecurityContextService securityContextService;

    @PostMapping
    @Operation(summary = "공동구매 상품 등록", description = "새로운 공동구매 상품을 등록합니다.")
    public ApiResponse<Product> createProduct(@RequestBody ProductCreateRequest productCreateRequest) {
        Long userId = securityContextService.getCurrentUserId();
        Product product = productService.createProduct(productCreateRequest, userId);
        return ApiResponse.success("상품이 성공적으로 등록되었습니다", product);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "공동구매 상품 상세 조회", description = "특정 공동구매 상품의 상세 정보를 조회합니다.")
    public ApiResponse<Product> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ApiResponse.success("상품을 성공적으로 조회했습니다", product);
    }

    @GetMapping
    @Operation(summary = "공동구매 상품 목록 조회", description = "전체 공동구매 상품 목록을 조회합니다.")
    public ApiResponse<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ApiResponse.success("상품 목록을 성공적으로 조회했습니다", products);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "공동구매 상품 수정", description = "기존 공동구매 상품의 정보를 수정합니다.")
    public ApiResponse<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductCreateRequest productCreateRequest
    ) {
        Long userId = securityContextService.getCurrentUserId();
        Product updatedProduct = productService.updateProduct(productId, productCreateRequest, userId);
        return ApiResponse.success("상품이 성공적으로 수정되었습니다", updatedProduct);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "공동구매 상품 삭제", description = "지정한 공동구매 상품을 삭제합니다.")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        Long userId = securityContextService.getCurrentUserId();
        productService.deleteProduct(productId, userId);
        return ApiResponse.success("상품이 성공적으로 삭제되었습니다", null);
    }

    @GetMapping("/search")
    @Operation(summary = "공동구매 상품 검색", description = "타이틀, 카테고리, 가격, 상태를 기준으로 공동구매 상품을 검색합니다.")
    public ApiResponse<List<Product>> searchProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) ProductStatus status
    ) {
        ProductSearchRequest searchRequest = new ProductSearchRequest(title, categoryCode, priceMin, priceMax, status);
        List<Product> products = productService.searchProducts(searchRequest);
        return ApiResponse.success("상품 검색 결과를 성공적으로 조회했습니다", products);
    }
}

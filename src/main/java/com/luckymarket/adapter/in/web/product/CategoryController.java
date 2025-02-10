package com.luckymarket.adapter.in.web.product;

import com.luckymarket.domain.entity.product.Category;
import com.luckymarket.domain.exception.product.CategoryException;
import com.luckymarket.application.service.product.CategoryService;
import com.luckymarket.adapter.in.web.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "카테고리 API", description = "카테고리 관련 API")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "전체 카테고리 목록 조회", description = "전체 카테고리 목록을 조회합니다.")
    public ApiResponse<List<Category>> getCategories() {
        try {
            List<Category> categories = categoryService.getCategories();
            return ApiResponse.success("카테고리 목록 조회 성공", categories);
        } catch (CategoryException e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/parents")
    @Operation(summary = "상위 카테고리 조회", description = "상위 카테고리를 조회합니다.")
    public ApiResponse<List<Category>> getParentCategories() {
        try {
            List<Category> categories = categoryService.getParentCategories();
            return ApiResponse.success("상위 카테고리 조회 성공", categories);
        } catch (CategoryException e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/subcategories/{parentId}")
    @Operation(summary = "하위 카테고리 조회", description = "특정 상위 카테고리에 속한 하위 카테고리를 조회합니다.")
    public ApiResponse<List<Category>> getSubCategories(@PathVariable Long parentId) {
        try {
            List<Category> subCategories = categoryService.getSubCategories(parentId);
            return ApiResponse.success("하위 카테고리 조회 성공", subCategories);
        } catch (CategoryException e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "카테고리 코드로 조회", description = "특정 카테고리 코드를 기준으로 카테고리를 조회합니다.")
    public ApiResponse<Category> getCategoryByCode(@PathVariable String code) {
        try {
            Category category = categoryService.getCategoryByCode(code);
            return ApiResponse.success("카테고리 조회 성공", category);
        } catch (CategoryException e) {
            return ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST.value());
        }
    }
}

package com.cuonglm.ecommerce.backend.category.controller;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.category.dto.internal.*;
import com.cuonglm.ecommerce.backend.category.service.CategoryService;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController – Controller của Category.
 * @author cuonglmptit
 * @since Tuesday, 04 August 2026
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryTreeNodeDTO>>> getCategoryTree() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryTree()));
    }

    @GetMapping("/roots")
    public ResponseEntity<ApiResponse<List<CategoryInfoDTO>>> getRootCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getRootCategories()));
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<ApiResponse<List<CategoryInfoDTO>>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getChildrenCategories(parentId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryInfoDTO>>> search(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.searchCategories(keyword)));
    }

    @GetMapping("/{categoryId}/attributes")
    public ResponseEntity<ApiResponse<List<CategoryAttributeInfoDTO>>> getCategoryAttributes(
            @PathVariable Long categoryId,
            @RequestParam(value = "type", required = false) AttributeType type
    ) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryAttributes(categoryId, type)));
    }
}

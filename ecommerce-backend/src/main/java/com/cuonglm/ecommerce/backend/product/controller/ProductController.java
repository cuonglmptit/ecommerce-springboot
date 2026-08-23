package com.cuonglm.ecommerce.backend.product.controller;

import com.cuonglm.ecommerce.backend.core.pagination.PageResponse;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductSearchFilterRequest;
import com.cuonglm.ecommerce.backend.product.dto.external.ProductSummaryResponse;
import com.cuonglm.ecommerce.backend.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * ProductController – Controller cho Product.
 *
 * @author cuonglmptit
 * @since Wednesday, 23 July 2025
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lấy chi tiết sản phẩm công khai (chỉ xem được sản phẩm ACTIVE).
     */
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getPublicProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tìm kiếm và phân trang danh sách sản phẩm công khai trên sàn.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> searchProducts(
            @ModelAttribute ProductSearchFilterRequest filter,
            @PageableDefault(size = 20, sort = "audit.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductSummaryResponse> response = productService.searchPublicProducts(filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

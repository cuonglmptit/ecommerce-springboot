package com.cuonglm.ecommerce.backend.product.controller;

import com.cuonglm.ecommerce.backend.core.pagination.PageResponse;
import com.cuonglm.ecommerce.backend.core.response.ApiResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.*;
import com.cuonglm.ecommerce.backend.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * SellerProductController – API dành riêng cho Seller quản lý sản phẩm trong gian hàng.
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
@RestController
@RequestMapping("/api/v1/seller/products")
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    private final ProductService productService;

    public SellerProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Tạo mới sản phẩm và các biến thể trong Shop.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("201", "Tạo sản phẩm thành công.", response));
    }

    /**
     * Cập nhật thông tin toàn diện của sản phẩm.
     */
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("200", "Cập nhật sản phẩm thành công.", response));
    }

    /**
     * Cập nhật nhanh trạng thái sản phẩm (ACTIVE, INACTIVE, DRAFT...).
     */
    @PatchMapping("/{id:[0-9]+}/status")
    public ResponseEntity<ApiResponse<Void>> updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request) {
        productService.updateProductStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("200", "Cập nhật trạng thái sản phẩm thành công.", null));
    }

    /**
     * Lấy chi tiết sản phẩm cho Seller (xem được cả bản nháp, tạm ẩn).
     */
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable Long id) {
        ProductResponse response = productService.getProductDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Lấy danh sách sản phẩm phân trang trong Shop của Seller.
     */
    @GetMapping("/shop/{shopId:[0-9]+}")
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getShopProducts(
            @PathVariable Long shopId,
            @ModelAttribute ProductSearchFilterRequest filter,
            @PageableDefault(size = 20, sort = "audit.createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ProductSummaryResponse> response = productService.getShopProducts(shopId, filter, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Xóa mềm (Soft delete) sản phẩm.
     */
    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("200", "Xóa sản phẩm thành công.", null));
    }
}
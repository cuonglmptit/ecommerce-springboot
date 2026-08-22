package com.cuonglm.ecommerce.backend.product.service;

import com.cuonglm.ecommerce.backend.core.pagination.PageResponse;
import com.cuonglm.ecommerce.backend.product.dto.external.*;
import org.springframework.data.domain.Pageable;

/**
 * ProductService – Interface mô tả các phương thức thao tác với Product.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
public interface ProductService {

    // 1. Luồng Ghi
    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(Long productId, UpdateProductRequest request);

    void updateProductStatus(Long productId, UpdateProductStatusRequest request);

    void deleteProduct(Long productId);

    // 2. Luồng Đọc
    ProductResponse getProductDetail(Long productId);

    ProductResponse getPublicProductDetail(Long productId);

    PageResponse<ProductSummaryResponse> searchPublicProducts(ProductSearchFilterRequest filter, Pageable pageable);

    PageResponse<ProductSummaryResponse> getShopProducts(Long shopId, ProductSearchFilterRequest filter, Pageable pageable);
}
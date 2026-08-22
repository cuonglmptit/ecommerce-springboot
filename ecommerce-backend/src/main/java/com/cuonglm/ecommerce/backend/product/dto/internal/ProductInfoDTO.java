package com.cuonglm.ecommerce.backend.product.dto.internal;

import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.projection.ProductDetailInfoView;

/**
 * ProductInfoDTO – DTO nội bộ phục vụ giao tiếp giữa ProductService và các Service khác (Order, Cart, Inventory...).
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public record ProductInfoDTO(
        Long id,
        Long shopId,
        Long categoryId,
        String name,
        String slug,
        ProductStatus status
) {
    public static ProductInfoDTO fromView(ProductDetailInfoView view) {
        if (view == null) return null;
        return new ProductInfoDTO(
                view.getId(),
                view.getShop() != null ? view.getShop().getId() : null,
                view.getCategory() != null ? view.getCategory().getId() : null,
                view.getName(),
                view.getSlug(),
                view.getStatus()
        );
    }
}
package com.cuonglm.ecommerce.backend.product.dto.external;

import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * ProductSearchFilterRequest – Query parameters cho bộ lọc tìm kiếm Product
 *
 * @author cuonglmptit
 * @since Saturday, 22 August 2026
 */
public record ProductSearchFilterRequest(
        String keyword,
        Long shopId,
        Long categoryId,
        List<Long> categoryIds,
        ProductStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStockOnly
) {}
package com.cuonglm.ecommerce.backend.category.dto.internal;

/**
 * CategoryInfoDTO – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public record CategoryInfoDTO(
        Long id,
        String name,
        String description,
        Integer depth,
        Integer sortOrder
) {
}

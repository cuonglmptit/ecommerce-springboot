package com.cuonglm.ecommerce.backend.category.dto.internal;

/**
 * CategoryView – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public interface CategoryInfoView {
    Long getId();
    String getName();
    String getDescription();
    Integer getDepth();
    Integer getSortOrder();
}

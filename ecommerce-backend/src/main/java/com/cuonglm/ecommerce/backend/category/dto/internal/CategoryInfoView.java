package com.cuonglm.ecommerce.backend.category.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;

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

    String getPath();

    Integer getDepth();

    Integer getSortOrder();

    ParentSummaryInfoView getParent();

    interface ParentSummaryInfoView {
        Long getId();
    }

    // Map danh sách con (ẩn khỏi JSON) để tính toán node lá
    @JsonIgnore
    Collection<ChildSummaryInfoView> getChildren();

    interface ChildSummaryInfoView {
        Long getId();
    }

    // Default method tự động kiểm tra xem danh mục có con không
    default Boolean getHasChildren() {
        return getChildren() != null && !getChildren().isEmpty();
    }
}

package com.cuonglm.ecommerce.backend.category.projection;

/**
 * CategoryInfoView – Projection lấy ngành hàng.
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

    Boolean getHasChildren();

    interface ParentSummaryInfoView {
        Long getId();
    }
}

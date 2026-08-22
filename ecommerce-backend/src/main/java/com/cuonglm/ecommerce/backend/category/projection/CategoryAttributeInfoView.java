package com.cuonglm.ecommerce.backend.category.projection;

import com.cuonglm.ecommerce.backend.attribute.projection.AttributeInfoView;
import com.cuonglm.ecommerce.backend.category.enums.FilterType;

/**
 * CategoryAttributeInfoView – Projection lấy thuộc tính ngành hàng.
 *
 * @author cuonglmptit
 * @since Tuesday, 04 August 2026
 */
public interface CategoryAttributeInfoView {
    Long getId();

    Integer getSortOrder();

    Boolean getFilterable();

    FilterType getFilterType();

    AttributeInfoView getAttribute();
}

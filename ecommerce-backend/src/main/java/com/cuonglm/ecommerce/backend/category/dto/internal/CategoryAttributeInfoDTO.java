package com.cuonglm.ecommerce.backend.category.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeInfoDTO;
import com.cuonglm.ecommerce.backend.category.enums.FilterType;

/**
 * CategoryAttributeInfoDTO – DTO cho {@link com.cuonglm.ecommerce.backend.category.entity.CategoryAttribute}
 *
 * @author cuonglmptit
 * @since Wednesday, 12 August 2026
 */
public record CategoryAttributeInfoDTO(
        Long id,
        Integer sortOrder,
        Boolean filterable,
        FilterType filterType,
        AttributeInfoDTO attribute
) {
    public static CategoryAttributeInfoDTO fromView(CategoryAttributeInfoView view) {
        if (view == null) {
            return null;
        }
        return new CategoryAttributeInfoDTO(
                view.getId(),
                view.getSortOrder(),
                view.getFilterable(),
                view.getFilterType(),
                AttributeInfoDTO.fromView(view.getAttribute())
        );
    }
}

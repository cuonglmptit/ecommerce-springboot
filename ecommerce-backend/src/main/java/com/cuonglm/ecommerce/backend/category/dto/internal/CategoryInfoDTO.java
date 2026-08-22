package com.cuonglm.ecommerce.backend.category.dto.internal;

import com.cuonglm.ecommerce.backend.category.projection.CategoryInfoView;

/**
 * CategoryInfoDTO – DTO thông tin category
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public record CategoryInfoDTO(
        Long id,
        String name,
        String description,
        String path,
        Integer depth,
        Integer sortOrder,
        Long parentId,
        Boolean hasChildren
) {
    public static CategoryInfoDTO fromView(CategoryInfoView view) {
        if (view == null) {
            return null;
        }
        return new CategoryInfoDTO(
                view.getId(),
                view.getName(),
                view.getDescription(),
                view.getPath(),
                view.getDepth(),
                view.getSortOrder(),
                view.getParent() != null ? view.getParent().getId() : null,
                view.getHasChildren() != null && view.getHasChildren()
        );
    }
}
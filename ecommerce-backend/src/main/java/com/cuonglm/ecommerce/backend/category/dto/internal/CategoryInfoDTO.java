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
        String path,
        Integer depth,
        Integer sortOrder,
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
                view.getHasChildren()
        );
    }
}
package com.cuonglm.ecommerce.backend.category.dto.internal;

import java.util.List;

/**
 * CategoryTreeNodeDTO – DTO dạng cây lồng nhau cho Frontend.
 *
 * @author cuonglmptit
 * @since Saturday, 08 August 2026
 */
public record CategoryTreeNodeDTO(
        Long id,
        String name,
        String description,
        String path,
        Integer depth,
        Integer sortOrder,
        Boolean hasChildren,
        Long parentId,
        List<CategoryTreeNodeDTO> children
) {
    public static CategoryTreeNodeDTO of(
            CategoryInfoView view,
            boolean hasChildren,
            List<CategoryTreeNodeDTO> children
    ) {
        if (view == null) {
            return null;
        }

        Long parentId = (view.getParent() != null) ? view.getParent().getId() : null;

        return new CategoryTreeNodeDTO(
                view.getId(),
                view.getName(),
                view.getDescription(),
                view.getPath(),
                view.getDepth(),
                view.getSortOrder(),
                hasChildren,
                parentId,
                children
        );
    }

    /*
    private static Long extractParentIdFromPath(String path) {
        if (path == null) return null;
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            try {
                return Long.parseLong(parts[parts.length - 2]);
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }
     */
}
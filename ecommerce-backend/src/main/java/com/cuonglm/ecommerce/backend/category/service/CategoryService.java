package com.cuonglm.ecommerce.backend.category.service;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.category.dto.internal.*;
import com.cuonglm.ecommerce.backend.category.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * CategoryService – Định nghĩa các phương thức liên quan đến Category.
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public interface CategoryService {
    Optional<CategoryInfoDTO> findCategoryInfoById(Long categoryId);

    Category getCategoryReference(Long categoryId);

    List<CategoryInfoDTO> getRootCategories();

    List<CategoryInfoDTO> getChildrenCategories(Long parentId);

    List<CategoryInfoDTO> searchCategories(String keyword);

    List<CategoryAttributeInfoDTO> getCategoryAttributes(Long categoryId, AttributeType type);

    List<CategoryTreeNodeDTO> getCategoryTree();
}

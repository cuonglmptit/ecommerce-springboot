package com.cuonglm.ecommerce.backend.category.service;

import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoDTO;
import com.cuonglm.ecommerce.backend.category.entity.Category;

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
}

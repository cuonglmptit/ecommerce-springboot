package com.cuonglm.ecommerce.backend.category.repository;

import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoView;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * CategoryRepository – Repository cho {@link Category}.
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<CategoryInfoView> findCategoryInfoById(Long categoryId);

    Optional<Category> findByName(String catName);
}

package com.cuonglm.ecommerce.backend.category.repository;

import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryInfoView;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryRepository – Repository cho {@link Category}.
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<CategoryInfoView> findCategoryInfoById(Long categoryId);

    Optional<Category> findByName(String catName);

    // 1. Lấy danh mục gốc (Level 0)
    @Query("""
        SELECT c FROM Category c
        WHERE c.parent IS NULL AND c.status = :status
        ORDER BY c.sortOrder ASC
    """)
    List<CategoryInfoView> findRootCategories(@Param("status") BasicStatus status);

    // 2. Lấy danh mục con trực tiếp theo parentId
    @Query("""
        SELECT c FROM Category c
        WHERE c.parent.id = :parentId AND c.status = :status
        ORDER BY c.sortOrder ASC
    """)
    List<CategoryInfoView> findChildrenCategories(@Param("parentId") Long parentId, @Param("status") BasicStatus status);

    // 3. Tìm kiếm danh mục theo tên cho Typeahead
    @Query("""
        SELECT c FROM Category c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.status = :status
        ORDER BY c.depth ASC, c.sortOrder ASC
    """)
    List<CategoryInfoView> searchCategories(@Param("keyword") String keyword, @Param("status") BasicStatus status);

    @Query("""
        SELECT c FROM Category c
        WHERE c.status = :status
        ORDER BY c.depth ASC, c.sortOrder ASC
    """)
    List<CategoryInfoView> findAllActiveCategories(@Param("status") BasicStatus status);
}

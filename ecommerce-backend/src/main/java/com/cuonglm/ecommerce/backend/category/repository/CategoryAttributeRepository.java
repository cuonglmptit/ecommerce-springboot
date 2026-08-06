package com.cuonglm.ecommerce.backend.category.repository;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;
import com.cuonglm.ecommerce.backend.category.dto.internal.CategoryAttributeInfoView;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * CategoryAttributeRepository – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 07 December 2025
 */
@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {
    Collection<Object> findByCategoryAndAttribute(Category category, Attribute attribute);

    @Query("""
        SELECT ca FROM CategoryAttribute ca
        JOIN FETCH ca.attribute a
        WHERE ca.category.id = :categoryId
          AND a.status = :attributeStatus
          AND (:attributeType IS NULL OR a.type = :attributeType)
        ORDER BY ca.sortOrder ASC
    """)
    List<CategoryAttributeInfoView> findCategoryAttributes(
            @Param("categoryId") Long categoryId,
            @Param("attributeStatus") AttributeStatus attributeStatus,
            @Param("attributeType") AttributeType attributeType
    );
}

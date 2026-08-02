package com.cuonglm.ecommerce.backend.category.repository;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.category.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

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
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute, Long> {
    Collection<Object> findByCategoryAndAttribute(Category category, Attribute attribute);
}

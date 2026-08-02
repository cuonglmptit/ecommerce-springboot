package com.cuonglm.ecommerce.backend.product.repository;

import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductVariantRepository – Repository cho ProductVariant.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
}

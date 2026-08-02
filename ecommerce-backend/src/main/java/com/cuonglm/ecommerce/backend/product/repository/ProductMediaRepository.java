package com.cuonglm.ecommerce.backend.product.repository;

import com.cuonglm.ecommerce.backend.product.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * ProductMediaRepository – Repository cho ProductMedia.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, UUID> {
}

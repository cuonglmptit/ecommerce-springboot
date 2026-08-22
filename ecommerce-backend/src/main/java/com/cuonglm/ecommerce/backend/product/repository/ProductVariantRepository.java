package com.cuonglm.ecommerce.backend.product.repository;

import com.cuonglm.ecommerce.backend.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * ProductVariantRepository – Repository cho ProductVariant.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    // 1. Tìm biến thể theo SKU
    Optional<ProductVariant> findBySku(String sku);

    // 2. Kiểm tra tồn tại của một SKU
    boolean existsBySku(String sku);

    // 3. Batch check nhiều SKU có tồn tại trong hệ thống hay chưa
    boolean existsBySkuIn(Set<String> skus);

}

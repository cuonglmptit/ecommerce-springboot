package com.cuonglm.ecommerce.backend.product.repository;

import com.cuonglm.ecommerce.backend.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ProductRepository – Repository của Product.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}

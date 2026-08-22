package com.cuonglm.ecommerce.backend.product.repository;

import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.product.projection.ProductDetailInfoView;
import com.cuonglm.ecommerce.backend.product.projection.ProductSummaryInfoView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * ProductRepository – Repository của Product.
 *
 * @author cuonglmptit
 * @since Tuesday, 25 November 2025
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 1. Chi tiết sản phẩm qua Projection (Dùng cho Seller Dashboard)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<ProductDetailInfoView> findDetailInfoById(@Param("id") Long id);

    // 2. Chi tiết sản phẩm qua Projection (Dùng cho Khách mua hàng - chỉ xem hàng ACTIVE)
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.status = :status")
    Optional<ProductDetailInfoView> findDetailInfoByIdAndStatus(
            @Param("id") Long id,
            @Param("status") ProductStatus status
    );

    // 3. Phân trang tìm kiếm Storefront (Dành cho khách - luôn fix cứng status = ACTIVE)
    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.slug) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:shopId IS NULL OR p.shop.id = :shopId)")
    Page<ProductSummaryInfoView> searchPublicProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("shopId") Long shopId,
            Pageable pageable
    );

    // 4. Phân trang quản lý sản phẩm cho Seller (Lọc theo trạng thái bất kỳ của Shop)
    @Query("SELECT p FROM Product p WHERE p.shop.id = :shopId " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<ProductSummaryInfoView> searchShopProducts(
            @Param("shopId") Long shopId,
            @Param("keyword") String keyword,
            @Param("status") ProductStatus status,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    // 5. Kiểm tra trùng lặp tên sản phẩm trong cùng Shop
    boolean existsByShopIdAndName(Long shopId, String name);

    // 6. Kiểm tra trùng lặp slug trong cùng Shop
    boolean existsByShopIdAndSlug(Long shopId, String slug);

}

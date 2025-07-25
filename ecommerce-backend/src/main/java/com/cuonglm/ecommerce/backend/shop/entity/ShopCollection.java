package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.product.entity.Product;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * ShopCollection – Thực thể Danh mục của 1 Shop.
 *
 * <p>
 * Danh mục là tập hợp các sản phẩm của 1 Shop để người dùng có thể vào Shop và xem/lọc theo danh mục.
 * Ví dụ: "Sản phẩm bán chạy", "Sale cuối tuần", "Hàng mới về"... * </p>
 *
 * @author cuonglmptit
 * @since Sunday, 20 July 2025
 */
@Entity
@Table(name = "shop_collections")
public class ShopCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "shop_collection_products",
            joinColumns = @JoinColumn(name = "collection_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BasicStatus status = BasicStatus.ACTIVE;

    //<editor-fold desc="Audit metadata">
    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", updatable = false)
    private User createdBy;
    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by_id")
    private User modifiedBy;
    @LastModifiedDate
    private Instant modifiedAt;
    //</editor-fold>
}

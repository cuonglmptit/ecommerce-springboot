package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.media.Media;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
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
 * Product – Sản phẩm của hệ thống Ecommerce.
 *
 * <p>
 * Thực thể Product của một shop.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 15 July 2025
 */
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @OneToMany(mappedBy = "product", orphanRemoval = true)
    private List<ProductMedia> media;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

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

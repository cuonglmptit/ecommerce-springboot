package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.category.entity.Category;
import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.SpecificationSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductStatus;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_shop_id", columnList = "shop_id"),
                @Index(name = "idx_product_category_id", columnList = "category_id"),
                @Index(name = "idx_product_status", columnList = "status"),
                @Index(name = "idx_product_slug", columnList = "slug"),
                @Index(name = "idx_product_name", columnList = "name")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 255)
    private String slug;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "specifications", columnDefinition = "jsonb")
    private List<SpecificationSnapshot> specifications = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProductStatus status = ProductStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media", columnDefinition = "jsonb")
    private List<ProductMediaSnapshot> media = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> variants = new ArrayList<>();

    @Version
    private Long version;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    //<editor-fold desc="Getter/Setters">

    public void addVariant(ProductVariant variant) {
        variants.add(variant);
        variant.setProduct(this);
    }

    public AuditMetadata getAudit() {
        return audit;
    }

    public void setAudit(AuditMetadata audit) {
        this.audit = (audit != null) ? audit : new AuditMetadata();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SpecificationSnapshot> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationSnapshot> specifications) {
        this.specifications = (specifications != null) ? specifications : new ArrayList<>();
    }

    public List<ProductMediaSnapshot> getMedia() {
        return media;
    }

    public void setMedia(List<ProductMediaSnapshot> media) {
        this.media = (media != null) ? media : new ArrayList<>();
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getVersion() {
        return version;
    }
//</editor-fold>
}

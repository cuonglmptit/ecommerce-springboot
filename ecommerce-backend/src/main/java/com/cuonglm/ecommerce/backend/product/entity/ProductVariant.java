package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.product.entity.snapshot.ProductMediaSnapshot;
import com.cuonglm.ecommerce.backend.product.entity.snapshot.VariantAttributeSnapshot;
import com.cuonglm.ecommerce.backend.product.enums.ProductVariantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductVariant – Đại diện cho một biến thể cụ thể của sản phẩm (Product).
 *
 * <p>
 * Một sản phẩm (Product) có thể có nhiều biến thể (Variant),
 * được phân biệt bởi các thuộc tính như Màu sắc, Kích thước...
 * </p>
 *
 * <p>
 * Ví dụ: Product = "Áo thun Nam" có thể có:
 * <ul>
 *     <li>Variant 1: Màu Đỏ, Size M</li>
 *     <li>Variant 2: Màu Đen, Size L</li>
 * </ul>
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 22 July 2025
 */
@Entity
@Table(name = "product_variants")
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(unique = true)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    @Min(value = 0, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @Column(precision = 19, scale = 2)
    @Min(value = 0, message = "Giá khuyến mãi phải lớn hơn 0")
    private BigDecimal salePrice;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private List<VariantAttributeSnapshot> attributes = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media", columnDefinition = "jsonb")
    private List<ProductMediaSnapshot> media = new ArrayList<>();

    @Column(nullable = false)
    private ProductVariantStatus status;

    //<editor-fold desc="Getters/Setters">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public List<VariantAttributeSnapshot> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<VariantAttributeSnapshot> attributes) {
        this.attributes = attributes;
    }

    public List<ProductMediaSnapshot> getMedia() {
        return media;
    }

    public void setMedia(List<ProductMediaSnapshot> media) {
        this.media = (media != null) ? media : new ArrayList<>();
    }

    public ProductVariantStatus getStatus() {
        return status;
    }

    public void setStatus(ProductVariantStatus status) {
        this.status = status;
    }

    //</editor-fold>
}

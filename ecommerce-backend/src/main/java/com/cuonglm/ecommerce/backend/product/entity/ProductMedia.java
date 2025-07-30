package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.media.Media;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * ProductMedia – Lớp lưu hình ảnh của sản phẩm/biến thể của sản phẩm.
 *
 * <p>
 * Lớp lưu hình ảnh của sản phẩm/variant của sản phẩm.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 22 July 2025
 */
@Entity
@Table(name = "product_media")
public class ProductMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * nullable – ảnh dùng cho product chung
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    /**
     * nullable – ảnh dùng cho 1 biến thể product cụ thể
     */
    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = true)
    private ProductVariant variant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    private boolean isThumbnail = false;

    private int sortOrder = 0;

    public boolean isForVariant() {
        return variant != null;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (product == null && variant == null) {
            throw new IllegalStateException("ProductMedia phải gắn với Product hoặc Variant");
        }
    }

}
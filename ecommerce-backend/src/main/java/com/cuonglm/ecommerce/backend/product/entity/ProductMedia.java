package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.media.entity.Media;
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
     * Ảnh phải được gắn với một Product, du` la` cho Variant hay la` dung` chung
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
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

    //<editor-fold desc="Getters/Setters">

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductVariant getVariant() {
        return variant;
    }

    public void setVariant(ProductVariant variant) {
        this.variant = variant;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public boolean isThumbnail() {
        return isThumbnail;
    }

    public void setThumbnail(boolean thumbnail) {
        isThumbnail = thumbnail;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    //</editor-fold>
}
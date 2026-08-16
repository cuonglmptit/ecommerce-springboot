package com.cuonglm.ecommerce.backend.media.entity;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaProvider;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * Media – Lưu thông tin 1 media (ảnh, video, v.v.)
 *
 * <p>
 * Media có thể là ảnh (image), video, hoặc loại khác. Được dùng cho Product, Category, Avatar...
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 22 July 2025
 */
@Entity
@Table(name = "media",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "external_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Media {
    /**
     * UUID với GenerationType.UUID (JPA 3.1+)
     */
    @PrePersist
    public void ensureId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Loại variantMedia (ảnh, video,...)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type = MediaType.IMAGE;

    /**
     * Bên cung cấp lưu trữ (Cloudinary, Vimeo, Youtube,...)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "provider")
    private MediaProvider provider = MediaProvider.CLOUDINARY;

    /**
     * Đường dẫn variantMedia
     */
    @Column(nullable = false)
    private String url;

    /**
     * ID từ provider để xóa (Cloudinary publicId, YouTube videoId, AWS S3 key, v.v.)
     */
    @Column(nullable = false, name = "external_id")
    private String externalId;

    /**
     * Mô tả variantMedia (accessibility, SEO)
     */
    private String alt;

    // Tên ảnh hiển thị
    private String title;

    /**
     * jpg, png, webp...
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaFormat format;

    // Soft delete
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BasicStatus status = BasicStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    //<editor-fold desc="Audit metadata">
    @CreatedBy
    @JoinColumn(name = "created_by_id", updatable = false)
    private Long createdById;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedBy
    @JoinColumn(name = "modified_by_id")
    private Long modifiedById;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant modifiedAt;
    //</editor-fold>

    //<editor-fold desc="Getters/Setters">
    public User getUploader() {
        return uploader;
    }

    public BasicStatus getStatus() {
        return status;
    }

    public MediaFormat getFormat() {
        return format;
    }

    public String getTitle() {
        return title;
    }

    public String getAlt() {
        return alt;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getUrl() {
        return url;
    }

    public MediaProvider getProvider() {
        return provider;
    }

    public MediaType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public void setStatus(BasicStatus status) {
        this.status = status;
    }

    public void setFormat(MediaFormat format) {
        this.format = format;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setProvider(MediaProvider provider) {
        this.provider = provider;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public void setId(UUID id) {
        this.id = id;
    }
//</editor-fold>
}

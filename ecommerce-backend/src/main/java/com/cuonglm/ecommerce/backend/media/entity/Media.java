package com.cuonglm.ecommerce.backend.media.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.media.enums.MediaFormat;
import com.cuonglm.ecommerce.backend.media.enums.MediaProvider;
import com.cuonglm.ecommerce.backend.media.enums.MediaScope;
import com.cuonglm.ecommerce.backend.media.enums.MediaType;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@Table(
        name = "media",
        indexes = {
                @Index(name = "idx_media_checksum", columnList = "checksum"),
                @Index(name = "idx_media_created_by", columnList = "created_by_id"),
                @Index(name = "idx_media_scope", columnList = "scope")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_media_provider_external", columnNames = {"provider", "external_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Media {
    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "checksum", length = 64, nullable = false)
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MediaScope scope = MediaScope.PUBLIC;

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

    /**
     * UUID với GenerationType.UUID (JPA 3.1+)
     */
    @PrePersist
    public void ensureIdAndChecksum() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.checksum == null) {
            this.checksum = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        }
    }

    public static Media of(
            String checksum,
            MediaScope scope,
            String url,
            String externalId,
            String title,
            String alt,
            MediaFormat format,
            MediaProvider provider
    ) {
        Media media = new Media();
        media.setChecksum(checksum);
        media.setScope(scope != null ? scope : MediaScope.PUBLIC);
        media.setUrl(url);
        media.setExternalId(externalId);
        media.setTitle(title != null && !title.isBlank() ? title.trim() : "media-asset");
        media.setAlt(alt != null && !alt.isBlank() ? alt.trim() : null);
        media.setFormat(format);
        media.setType(format != null ? format.getMediaType() : MediaType.IMAGE);
        media.setProvider(provider != null ? provider : MediaProvider.CLOUDINARY);
        media.setStatus(BasicStatus.ACTIVE);
        return media;
    }

    //<editor-fold desc="Getters/Setters">
    public MediaScope getScope() {
        return scope;
    }

    public void setScope(MediaScope scope) {
        this.scope = scope;
    }

    public BasicStatus getStatus() {
        return status;
    }

    public void setStatus(BasicStatus status) {
        this.status = status;
    }

    public MediaFormat getFormat() {
        return format;
    }

    public void setFormat(MediaFormat format) {
        this.format = format;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MediaProvider getProvider() {
        return provider;
    }

    public void setProvider(MediaProvider provider) {
        this.provider = provider;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}

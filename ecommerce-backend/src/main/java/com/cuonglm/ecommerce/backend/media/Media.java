package com.cuonglm.ecommerce.backend.media;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

/**
 * Media – Lưu thông tin media dùng chung (ảnh, video, v.v.)
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
public class Media {
    /**
     * UUID với GenerationType.UUID (JPA 3.1+)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Loại media (ảnh, video,...)
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
     * Đường dẫn media
     */
    @Column(nullable = false, unique = true)
    private String url;

    /**
     * ID từ provider để xóa (Cloudinary publicId, YouTube videoId, AWS S3 key, v.v.)
     */
    @Column(nullable = false, name = "external_id")
    private String externalId;

    /**
     * Mô tả media (accessibility, SEO)
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
    private User uploader;

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

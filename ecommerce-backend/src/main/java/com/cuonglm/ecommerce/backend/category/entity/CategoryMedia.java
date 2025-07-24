package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.media.Media;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * CategoryMedia – Lớp lưu hình ảnh của một danh mục.
 *
 * <p>
 * Lưu hình ảnh của category để có thể hiển thị khi truy cập theo danh mục.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 23 July 2025
 */
@Entity
@Table(name = "category_media")
public class CategoryMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

    private boolean isThumbnail = false;

    private int sortOrder = 0;
}

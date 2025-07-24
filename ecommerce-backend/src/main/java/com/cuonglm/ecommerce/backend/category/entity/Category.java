package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
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
 * Category – Phân loại sản phẩm.
 *
 * <p>
 * Thực thể Category. Cài đặt theo kiểu Materialized Path (nhưng là Parent-Child mở rộng ra để Breadcrumb cho dễ)
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 15 July 2025
 */
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryMedia> media = new ArrayList<>();

    /**
     * Đường dẫn từ gốc đến node hiện tại, ví dụ: /1/4/6/
     */
    @Column(nullable = false)
    private String path;

    //<editor-fold desc="Parent-Child self-reference">
    // Quan hệ self-reference (không bắt buộc trong Materialized Path, nhưng hữu ích để duyệt lên xuống dễ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();
    //</editor-fold>

    // Tiện truy vấn filter theo level nếu cần
    @Column(nullable = false)
    private Integer depth = 0;

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

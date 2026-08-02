package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
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

    //<editor-fold desc="Getter/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CategoryMedia> getMedia() {
        return media;
    }

    public void setMedia(List<CategoryMedia> media) {
        this.media = media;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public BasicStatus getStatus() {
        return status;
    }

    public void setStatus(BasicStatus status) {
        this.status = status;
    }
    //</editor-fold>
}

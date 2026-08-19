package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.category.entity.snapshot.CategoryMediaSnapshot;
import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "media", columnDefinition = "jsonb")
    private List<CategoryMediaSnapshot> media = new ArrayList<>();

    /**
     * Đường dẫn từ gốc đến node hiện tại, ví dụ: /1/4/6/
     */
    @Column(nullable = false)
    private String path;

    // Quan hệ self-reference (không bắt buộc trong Materialized Path, nhưng hữu ích để duyệt lên xuống dễ)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @Column(name = "has_children", nullable = false)
    private Boolean hasChildren = false;

    // Tiện truy vấn filter theo level nếu cần
    @Column(nullable = false)
    private Integer depth = 0;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BasicStatus status = BasicStatus.ACTIVE;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    public void addChild(Category child) {
        if (child != null) {
            // Nếu child đã có parent cũ, phải xóa child khỏi parent cũ trước
            if (child.getParent() != null && !child.getParent().equals(this)) {
                child.getParent().removeChild(child);
            }

            this.children.add(child);
            child.setParent(this);
            this.hasChildren = true; // Cha mới bật cờ
        }
    }

    public void removeChild(Category child) {
        if (child != null) {
            this.children.remove(child);
            child.setParent(null);
            this.hasChildren = !this.children.isEmpty(); // Tự tắt cờ nếu không còn con nào
        }
    }

    @PrePersist
    public void prePersist() {
        if (this.sortOrder == null) this.sortOrder = 0;
        if (this.status == null) this.status = BasicStatus.ACTIVE;
        if (this.media == null) this.media = new ArrayList<>();

        this.hasChildren = (this.children != null && !this.children.isEmpty());
        if (this.parent != null) {
            this.depth = (this.parent.getDepth() != null ? this.parent.getDepth() : 0) + 1;
        } else {
            this.depth = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.hasChildren = (this.children != null && !this.children.isEmpty());
        if (this.parent != null) {
            this.depth = (this.parent.getDepth() != null ? this.parent.getDepth() : 0) + 1;
        } else {
            this.depth = 0;
        }
    }

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

    public List<CategoryMediaSnapshot> getMedia() {
        return media;
    }

    public void setMedia(List<CategoryMediaSnapshot> media) {
        this.media = (media != null) ? media : new ArrayList<>();
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

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}

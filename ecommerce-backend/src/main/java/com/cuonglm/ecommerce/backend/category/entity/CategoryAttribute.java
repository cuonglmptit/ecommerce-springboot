package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.category.enums.FilterType;
import jakarta.persistence.*;

/**
 * CategoryAttribute – Liên kết đến các Attribute liên quan đến ngành hàng này.
 *
 * <p>
 * Link đến các Attribute để sử dụng như filter thông số 1 ngành hàng, chọn thông tin chi tiết (specs) của một sản phẩm trong ngành hàng.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 29 July 2025
 */
@Entity
@Table(
        name = "category_attribute",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"category_id", "attribute_id"})
        }
)

public class CategoryAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Chỉ liên kết với global attribute
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Boolean isFilterable = true;


    @Column(name = "filter_type")
    @Enumerated(EnumType.STRING)
    private FilterType filterType = FilterType.CHECKBOX;

    // <editor-fold desc="Getters/Setters">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getFilterable() {
        return isFilterable;
    }

    public void setFilterable(Boolean filterable) {
        isFilterable = filterable;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    // </editor-fold>
}

package com.cuonglm.ecommerce.backend.category.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.category.enums.FilterType;
import jakarta.persistence.*;

/**
 * CategoryAttribute – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
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

}

package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import jakarta.persistence.*;

/**
 * ShopAttributeTemplateOption – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Tuesday, 29 July 2025
 */
@Entity
@Table(name = "shop_attribute_template_options")
public class ShopAttributeTemplateOption {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_option_id", nullable = false)
    private AttributeOption option;

    @Column(nullable = false)
    private Integer sortOrder = 0;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    //<editor-fold desc="Getters/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AttributeOption getOption() {
        return option;
    }

    public void setOption(AttributeOption option) {
        this.option = option;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
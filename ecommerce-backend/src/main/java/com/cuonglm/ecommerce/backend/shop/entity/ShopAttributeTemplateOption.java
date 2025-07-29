package com.cuonglm.ecommerce.backend.shop.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
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
}
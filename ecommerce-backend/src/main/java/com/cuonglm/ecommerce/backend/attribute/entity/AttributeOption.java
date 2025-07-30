package com.cuonglm.ecommerce.backend.attribute.entity;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * AttributeOption – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 26 July 2025
 */
@Entity
@Table(name = "attribute_options")
public class AttributeOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false)
    private String value; // Ví dụ: Đỏ, Đen, L, XL

    @Enumerated(EnumType.STRING)
    private AttributeScope scope = AttributeScope.GLOBAL;
    // Getter/setter...
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // null nếu GLOBAL

    //<editor-fold desc="Getters">
    public UUID getId() {
        return id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    public AttributeScope getScope() {
        return scope;
    }

    public Shop getShop() {
        return shop;
    }
    //</editor-fold>
}

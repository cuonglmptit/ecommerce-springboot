package com.cuonglm.ecommerce.backend.attribute.entity;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Attribute – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 26 July 2025
 */
@Entity
@Table(name = "attributes")
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name; // Ví dụ: Màu sắc, Kích thước

    // "COLOR", "SIZE" - for technical use
    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private AttributeScope scope = AttributeScope.GLOBAL;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop; // null nếu GLOBAL

    @Enumerated(EnumType.STRING)
    private BasicStatus status = BasicStatus.ACTIVE;
}

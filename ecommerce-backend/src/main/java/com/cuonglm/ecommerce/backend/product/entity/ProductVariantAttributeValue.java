package com.cuonglm.ecommerce.backend.product.entity;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import jakarta.persistence.*;

import java.util.UUID;

/**
 * ProductVariantAttributeValue – Lưu giá trị thuộc tính (AttributeOption)
 * mà một biến thể (ProductVariant) đang có.
 *
 * <p>
 * Ví dụ: Biến thể SKU = "TSHIRT-RED-M" sẽ có:
 * <ul>
 *     <li>AttributeOption = "Màu Đỏ"</li>
 *     <li>AttributeOption = "Size M"</li>
 * </ul>
 * </p>
 *
 * <p>
 * Đây là bảng mapping giữa ProductVariant và AttributeOption.
 * </p>
 *
 * @author cuonglmptit
 * @since Monday, 28 July 2025
 */
@Entity
@Table(name = "product_variant_attribute_values",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"variant_id", "attribute_id"})
        })

public class ProductVariantAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_option_id", nullable = false)
    private AttributeOption option;

    @PrePersist
    @PreUpdate
    private void validate() {
        // Validate attribute option thuộc về attribute
        if (!option.getAttribute().equals(attribute)) {
            throw new IllegalStateException("AttributeOption không thuộc về Attribute được chỉ định");
        }
    }
}

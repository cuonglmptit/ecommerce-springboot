package com.cuonglm.ecommerce.backend.product.entity.snapshot;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * VariantAttributeSnapshot – Lưu thông tin Attribute để phân loại cho một {@link com.cuonglm.ecommerce.backend.product.entity.ProductVariant}
 *
 * @author cuonglmptit
 * @since Saturday, 01 August 2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record VariantAttributeSnapshot(
        UUID attributeId,
        String attributeName,
        UUID optionId,
        String optionValue
) {
    public static VariantAttributeSnapshot fromInfo(AttributeOptionInfoDTO option) {
        if (option == null) return null;
        return new VariantAttributeSnapshot(
                option.attributeId(),
                option.attributeName(),
                option.id(),
                option.value()
        );
    }

    public static VariantAttributeSnapshot of(UUID attributeId, String attributeName, UUID optionId, String optionValue) {
        return new VariantAttributeSnapshot(attributeId, attributeName, optionId, optionValue);
    }
}

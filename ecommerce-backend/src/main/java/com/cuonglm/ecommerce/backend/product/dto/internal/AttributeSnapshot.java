package com.cuonglm.ecommerce.backend.product.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.dto.internal.AttributeOptionInfoDTO;

import java.util.UUID;

/**
 * AttributeSnapshot – Mo_ta_file
 *
 * <p>
 * Mo_ta_chi_tiet
 * </p>
 *
 * @author cuonglmptit
 * @since Saturday, 01 August 2026
 */
public record AttributeSnapshot(
        UUID attributeId,
        String attributeName,
        UUID optionId,
        String optionValue
) {
    public static AttributeSnapshot fromInfo(AttributeOptionInfoDTO option) {
        if (option == null) return null;
        return new AttributeSnapshot(
                option.attributeId(),
                option.attributeName(),
                option.id(),
                option.value()
        );
    }
}

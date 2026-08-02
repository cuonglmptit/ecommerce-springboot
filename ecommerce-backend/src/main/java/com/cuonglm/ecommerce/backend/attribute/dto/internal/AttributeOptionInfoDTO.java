package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;

import java.util.UUID;

/**
 * AttributeOptionInfo – Dto thông tin về AttributeOption.
 *
 * @author cuonglmptit
 * @since Sunday, 30 November 2025
 */
public record AttributeOptionInfoDTO(
        UUID id,
        String value,
        UUID attributeId,
        String attributeName,
        Long shopId,
        AttributeScope scope,
        AttributeStatus status
) {
}

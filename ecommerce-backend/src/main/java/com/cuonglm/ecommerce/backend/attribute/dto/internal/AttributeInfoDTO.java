package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;

import java.util.UUID;

/**
 * AttributeInfoDTO – Dto thông tin của Attribute.
 *
 * @author cuonglmptit
 * @since Sunday, 30 November 2025
 */
public record AttributeInfoDTO(
        UUID id,
        String name,
        String code,
        AttributeScope scope,
        Long shopId,
        AttributeStatus status
) {
}

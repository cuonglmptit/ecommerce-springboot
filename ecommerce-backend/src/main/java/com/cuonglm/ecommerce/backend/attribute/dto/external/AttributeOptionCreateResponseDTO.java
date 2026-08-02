package com.cuonglm.ecommerce.backend.attribute.dto.external;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;

import java.util.UUID;

/**
 * AttributeOptionCreateResponseDTO – DTO trả về thông tin chi tiết của AttributeOption.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttributeOptionCreateResponseDTO(
        UUID id,
        String value,
        AttributeScope scope,
        AttributeStatus status,
        Long shopId // ID của Shop sở hữu (null nếu GLOBAL)
) {
}
package com.cuonglm.ecommerce.backend.attribute.dto.external;

import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
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
        Long shopId
) {
    public static AttributeOptionCreateResponseDTO fromEntity(AttributeOption opt) {
        if (opt == null) return null;
        return new AttributeOptionCreateResponseDTO(
                opt.getId(),
                opt.getValue(),
                opt.getScope(),
                opt.getStatus(),
                opt.getShop() != null ? opt.getShop().getId() : null
        );
    }
}
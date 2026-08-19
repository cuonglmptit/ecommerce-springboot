package com.cuonglm.ecommerce.backend.attribute.dto.external;

import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeType;

import java.util.List;
import java.util.UUID;

/**
 * AttributeCreateResponseDTO – Dto phục vụ việc tạo trả về kết quả tạo Attribute.
 *
 * @author cuonglmptit
 * @since Tuesday, 02 December 2025
 */
public record AttributeResponse(
        UUID id,
        String name,
        String code,
        AttributeType type,
        AttributeScope scope,
        AttributeStatus status,
        Long shopId,
        List<AttributeOptionResponse> options
) {
    public static AttributeResponse fromEntity(Attribute attribute, List<AttributeOptionResponse> options) {
        if (attribute == null) return null;
        return new AttributeResponse(
                attribute.getId(),
                attribute.getName(),
                attribute.getCode(),
                attribute.getType(),
                attribute.getScope(),
                attribute.getStatus(),
                attribute.getShop() != null ? attribute.getShop().getId() : null,
                options
        );
    }
}
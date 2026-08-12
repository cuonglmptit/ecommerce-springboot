package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;

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
    public static AttributeInfoDTO fromView(AttributeInfoView view) {
        if (view == null) {
            return null;
        }
        return new AttributeInfoDTO(
                view.getId(),
                view.getName(),
                view.getCode(),
                view.getScope(),
                (view.getShop() != null) ? view.getShop().getId() : null,
                view.getStatus()
        );
    }
}

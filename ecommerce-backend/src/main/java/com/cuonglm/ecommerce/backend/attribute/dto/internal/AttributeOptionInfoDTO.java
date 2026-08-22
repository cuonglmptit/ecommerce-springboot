package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.attribute.projection.AttributeOptionInfoView;

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
    public static AttributeOptionInfoDTO fromView(AttributeOptionInfoView view) {
        if (view == null) return null;
        return new AttributeOptionInfoDTO(
                view.getId(),
                view.getValue(),
                view.getAttribute() != null ? view.getAttribute().getId() : null,
                view.getAttribute() != null ? view.getAttribute().getName() : null,
                view.getShop() != null ? view.getShop().getId() : null,
                view.getScope(),
                view.getStatus()
        );
    }

    public static AttributeOptionInfoDTO fromEntity(AttributeOption option) {
        if (option == null) return null;
        return new AttributeOptionInfoDTO(
                option.getId(),
                option.getValue(),
                option.getAttribute() != null ? option.getAttribute().getId() : null,
                option.getAttribute() != null ? option.getAttribute().getName() : null,
                option.getShop() != null ? option.getShop().getId() : null,
                option.getScope(),
                option.getStatus()
        );
    }
}

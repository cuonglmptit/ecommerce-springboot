package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;

import java.util.UUID;

/**
 * AttributeOptionInfoView – Interface để projection thông tin cho AttributeOption.
 *
 * @author cuonglmptit
 * @since Sunday, 30 November 2025
 */
public interface AttributeOptionInfoView {
    UUID getId();

    String getValue();

    UUID getAttributeId();

    Long getShopId();

    String getAttributeName();

    AttributeScope getScope();

    AttributeStatus getStatus();
}

package com.cuonglm.ecommerce.backend.attribute.dto.internal;

import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import com.cuonglm.ecommerce.backend.core.status.BasicStatus;

import java.util.UUID;

/**
 * AttributeInfoView – Class phục vụ Interface-based projection để lấy ra các trường cần thiết của attribute cho các service khác.
 *
 * @author cuonglmptit
 * @since Sunday, 30 November 2025
 */
public interface AttributeInfoView {
    UUID getId();

    String getName();

    String getCode();

    AttributeScope getScope();

    Long getShopId();

    AttributeStatus getStatus();
}

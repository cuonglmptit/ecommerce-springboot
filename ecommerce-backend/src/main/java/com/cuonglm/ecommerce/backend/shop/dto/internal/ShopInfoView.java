package com.cuonglm.ecommerce.backend.shop.dto.internal;

import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;

/**
 * ShopInfoView – Interface-Based để cho projection Shop.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public interface ShopInfoView {
    Long getId();

    String getName();

    String getDescription();

    ShopStatus getStatus();

    Long getOwnerId();
}

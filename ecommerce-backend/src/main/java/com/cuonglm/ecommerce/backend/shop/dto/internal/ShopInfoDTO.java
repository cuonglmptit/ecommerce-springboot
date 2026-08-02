package com.cuonglm.ecommerce.backend.shop.dto.internal;

import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;

/**
 * ShopInfoDTO – Dto cho internal service xem thông tin về shop.
 *
 * @author cuonglmptit
 * @since Friday, 28 November 2025
 */
public record ShopInfoDTO(
        Long id,
        String name,
        String description,
        ShopStatus status,
        Long ownerId
) {
}

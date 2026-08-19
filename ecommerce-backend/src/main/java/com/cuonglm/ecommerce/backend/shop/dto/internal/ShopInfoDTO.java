package com.cuonglm.ecommerce.backend.shop.dto.internal;

import com.cuonglm.ecommerce.backend.shop.entity.Shop;
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
    public static ShopInfoDTO fromView(ShopInfoView view) {
        if (view == null) return null;
        return new ShopInfoDTO(
                view.getId(),
                view.getName(),
                view.getDescription(),
                view.getStatus(),
                view.getOwnerId()
        );
    }

    public static ShopInfoDTO fromEntity(Shop shop) {
        if (shop == null) return null;
        Long ownerId = (shop.getOwner() != null) ? shop.getOwner().getId() : null;
        return new ShopInfoDTO(
                shop.getId(),
                shop.getName(),
                shop.getDescription(),
                shop.getStatus(),
                ownerId
        );
    }
}
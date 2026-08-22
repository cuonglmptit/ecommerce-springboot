package com.cuonglm.ecommerce.backend.shop.dto.external;

import com.cuonglm.ecommerce.backend.shop.projection.ShopInfoView;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;

/**
 * ShopCreationResultDTO – Mô_tả_ngắn_về_lớp.
 *
 * <p>
 * Mô_tả_chi_tiết.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public record ShopResponse(
        Long id,
        String name,
        String description,
        Long ownerId,
        ShopStatus status
) {
    public static ShopResponse fromEntity(Shop shop) {
        if (shop == null) return null;
        Long ownerId = (shop.getOwner() != null) ? shop.getOwner().getId() : null;
        return new ShopResponse(
                shop.getId(),
                shop.getName(),
                shop.getDescription(),
                ownerId,
                shop.getStatus()
        );
    }

    public static ShopResponse fromView(ShopInfoView view) {
        if (view == null) return null;
        return new ShopResponse(
                view.getId(),
                view.getName(),
                view.getDescription(),
                view.getOwnerId(),
                view.getStatus()
        );
    }
}
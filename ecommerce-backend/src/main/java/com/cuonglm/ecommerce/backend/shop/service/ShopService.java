package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.shop.dto.external.CreateShopRequest;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopResponse;
import com.cuonglm.ecommerce.backend.shop.dto.external.UpdateShopRequest;
import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;

import java.util.Optional;

/**
 * ShopService – Định nghĩa các phương thức liên quan đến Shop.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public interface ShopService {
    ShopResponse createShop(CreateShopRequest request);

    ShopResponse getCurrentShop();

    ShopResponse getShopById(Long shopId);

    ShopResponse updateCurrentShop(UpdateShopRequest request);

    void approveShop(Long shopId);

    // Phục vụ giao tiếp nội bộ giữa các Service
    Optional<ShopInfoDTO> findShopInfoById(Long shopId);

    Optional<ShopInfoDTO> findShopInfoByOwnerId(Long ownerId);

    Shop getShopReference(Long shopId);

    boolean hasPermission(Long shopId, ShopPermission requiredPermission);

    void validatePermission(Long shopId, ShopPermission requiredPermission);
}

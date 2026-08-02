package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateRequestDTO;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;
import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;

import java.util.Optional;

/**
 * ShopService – Định nghĩa các phương thức liên quan đến Shop.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
public interface ShopService {
    ShopCreateResponseDTO createShop(ShopCreateRequestDTO request);
    void approveShop(Long shopId);
    Optional<ShopInfoDTO> findShopInfoById(Long shopId);
    Shop getShopReference(Long shopId);
    Optional<ShopInfoDTO> findShopInfoByOwnerId(Long ownerId);
}
